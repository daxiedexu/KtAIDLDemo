package com.haixu.ktaidldemo;
import com.haixu.ktaidldemo.entity.MessageBean;

interface IReceiveServer {

    void receiveMessage(in MessageBean message);

}