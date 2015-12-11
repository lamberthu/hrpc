package com.lambert.hrpc.core.example.core;

import com.lambert.hrpc.core.RpcContext;
import com.lambert.hrpc.core.RpcServer;
import com.lambert.hrpc.core.example.HelloServiceImpl;

/**
 * Created by pc on 2015/12/11.
 */
public class RpcServerTest {

    public static void testServer () throws Exception{

        RpcContext context = new RpcContext();

        RpcServer rpcServer = new RpcServer(context);

        rpcServer.addService(new HelloServiceImpl());

        rpcServer.start();
    }

    public static void main(String[] args) throws Exception{
        testServer();
    }
}
