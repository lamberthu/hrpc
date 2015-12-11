package com.lambert.hrpc.example.core;

import com.lambert.hrpc.RpcContext;
import com.lambert.hrpc.RpcProxyFactory;
import com.lambert.hrpc.RpcServer;
import com.lambert.hrpc.example.HelloServiceImpl;
import com.lambert.hrpc.registry.ServiceRegistry;
import com.lambert.hrpc.registry.zookeeper.ZookeeperServiceRegistry;

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
