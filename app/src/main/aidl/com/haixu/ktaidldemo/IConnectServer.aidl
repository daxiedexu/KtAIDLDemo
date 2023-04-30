package com.haixu.ktaidldemo;

interface IConnectServer {

    //oneway：防止阻塞进程
    oneway void connect();

    void disconnect();

    boolean queryConnectStatus();

}