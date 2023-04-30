package com.haixu.ktaidldemo;
import com.haixu.ktaidldemo.entity.MessageBean;
import com.haixu.ktaidldemo.IReceiveServer;

interface IMessageServer {

    void sendMessage(in MessageBean bean);

    void regsiterMessageReceiveListener(IReceiveServer server);

    void unRegsiterMessageReceiveListener(IReceiveServer server);

}