package com.haixu.ktaidldemo

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haixu.ktaidldemo.databinding.ActivityMainBinding
import com.haixu.ktaidldemo.entity.MessageBean

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() , OnClickListener {

    private var viewBinding: ActivityMainBinding? = null

    private var iConnectServer: IConnectServer? = null
    private var iMessageServer: IMessageServer? = null
    private var iServerManger: IServerManger? = null

    private var iMessageReceiveServer: IReceiveServer.Stub = object :IReceiveServer.Stub(){
        override fun receiveMessage(message: MessageBean?) {
            val uiHandler = Handler(Looper.getMainLooper()!!)
            uiHandler.post {
                Toast.makeText(this@MainActivity, message!!.content, Toast.LENGTH_SHORT).show()
            }
        }

        //重写该方法排查aidl快速定位堆栈错误代码 https://www.bbsmax.com/A/6pdD44yKdw/
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (e: RuntimeException) {
                Log.w(TAG, "Unexpected remote exception", e);
                throw e;
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding!!.root)

        initView()
        initServer()
    }

    private fun initView() {
        viewBinding!!.connectBtn.setOnClickListener(this)
        viewBinding!!.disconnectBtn.setOnClickListener(this)
        viewBinding!!.connectStateBtn.setOnClickListener(this)

        viewBinding!!.sendMessageBtn.setOnClickListener(this)
        viewBinding!!.registerBtn.setOnClickListener(this)
        viewBinding!!.unregisterBtn.setOnClickListener(this)
    }

    /**
     * 和子进程建立连接
     */
    private fun initServer() {
        val intent = Intent(this@MainActivity, RemoteServer::class.java)
        bindService(intent, object : ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, ibinder: IBinder?) {
                iServerManger = IServerManger.Stub.asInterface(ibinder)
                iConnectServer = IConnectServer.Stub.asInterface(iServerManger!!.getServer(IConnectServer::class.java.simpleName))
                iMessageServer = IMessageServer.Stub.asInterface(iServerManger!!.getServer(IMessageServer::class.java.simpleName))
                Log.d(TAG, "onServiceConnected")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "onServiceDisconnected")
            }
        }, Service.BIND_AUTO_CREATE)
    }

    override fun onClick(view: View?) {
        val id = view!!.id

        if (id == R.id.connect_btn){
            if (iConnectServer != null){
                iConnectServer!!.connect()
            } else {
                Toast.makeText(this@MainActivity, "iConnectServer null", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == R.id.disconnect_btn){
            if (iConnectServer != null){
                iConnectServer!!.disconnect()
            } else {
                Toast.makeText(this@MainActivity, "iConnectServer null", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == R.id.connect_state_btn){
            if (iConnectServer != null){
                val queryConnectStatus = iConnectServer!!.queryConnectStatus()
                Toast.makeText(this@MainActivity, "iConnectServer connect state $queryConnectStatus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "iConnectServer null", Toast.LENGTH_SHORT).show()
            }
        }

        if (id == R.id.send_message_btn){
            if (iMessageServer != null){
                val message = MessageBean()
                message.content = "message form main"
                iMessageServer?.sendMessage(message)
            } else {
                Toast.makeText(this@MainActivity, "iMessageServer null", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == R.id.register_btn){
            if (iMessageServer != null){
                iMessageServer?.regsiterMessageReceiveListener(iMessageReceiveServer)
            } else {
                Toast.makeText(this@MainActivity, "iMessageServer null", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == R.id.unregister_btn){
            if (iMessageServer != null){
                iMessageServer?.unRegsiterMessageReceiveListener(iMessageReceiveServer)
            } else {
                Toast.makeText(this@MainActivity, "iMessageServer null", Toast.LENGTH_SHORT).show()
            }
        }
    }
}