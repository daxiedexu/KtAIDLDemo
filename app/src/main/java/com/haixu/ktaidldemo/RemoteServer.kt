package com.haixu.ktaidldemo

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.os.postDelayed
import com.haixu.ktaidldemo.IConnectServer.Stub
import com.haixu.ktaidldemo.entity.MessageBean
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TAG = "RemoteServer"
class RemoteServer : Service() {

    private val mUiHandler = Handler(Looper.myLooper()!!)
    private val mDelayHandler = Handler()
    private val mDelayRunnable: Runnable by lazy {
        Runnable {
            val size = mRemoteCallback.registeredCallbackCount
            for (i in 0 until size) {
                try {
                    val message = MessageBean()
                    message.content = "value from child"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mRemoteCallback.getRegisteredCallbackItem(i)
                            .receiveMessage(message)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            Log.d(TAG, "run Delay task")
            mDelayHandler.postDelayed(mDelayRunnable,1000)
        }
    }

    private val mRemoteCallback: RemoteCallbackList<IReceiveServer> = RemoteCallbackList()
    private var mScheduledThreadPoolExecutor: ScheduledThreadPoolExecutor? = null
    private var mScheduledFuture: ScheduledFuture<*>? = null

    private var isConnectServer: Boolean = false
    private val iConnectServer : IConnectServer = object : Stub() {
        override fun connect() {
            Thread.sleep(1000)
            isConnectServer = true
            mUiHandler.post {
                Toast.makeText(this@RemoteServer, "connect", Toast.LENGTH_LONG).show()
            }
            mDelayHandler.postDelayed(mDelayRunnable,0)
        }

        override fun disconnect() {
            isConnectServer = false
            mDelayHandler.removeCallbacks(mDelayRunnable)
            mUiHandler.post {
                Toast.makeText(this@RemoteServer, "disconnect", Toast.LENGTH_LONG).show()
            }
        }

        override fun queryConnectStatus(): Boolean {
            return isConnectServer
        }
    }

    private val iMessageServer: IMessageServer.Stub = object : IMessageServer.Stub() {
        override fun sendMessage(bean: MessageBean?) {
            mUiHandler.post {
                Toast.makeText(this@RemoteServer, bean!!.content, Toast.LENGTH_LONG).show()
            }
            bean!!.isSendMessageSuccess = isConnectServer
        }

        override fun regsiterMessageReceiveListener(server: IReceiveServer?) {
            mRemoteCallback.register(server)
            Log.d(TAG, "registerMessageReceiveListener")
        }

        override fun unRegsiterMessageReceiveListener(server: IReceiveServer?) {
            mRemoteCallback.unregister(server)
            Log.d(TAG, "unRegisterMessageReceiveListener")
        }
    }

    private val iServerManger :IServerManger.Stub = object: IServerManger.Stub() {
        override fun getServer(serverName: String?): IBinder? {
            if (serverName.equals(IConnectServer::class.java.simpleName)){
                return iConnectServer.asBinder()
            } else if (serverName.equals(IMessageServer::class.java.simpleName)){
                return iMessageServer.asBinder()
            }
            return null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iServerManger.asBinder()
    }

    override fun onCreate() {
        super.onCreate()
        mScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    }
}