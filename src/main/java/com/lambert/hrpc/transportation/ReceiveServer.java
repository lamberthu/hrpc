package com.lambert.hrpc.transportation;

/**
 * 接收客户端发来的数据
 * Created by pc on 2015/12/10.
 */
public interface ReceiveServer {

    void start() throws  Exception;

    void stop();

}
