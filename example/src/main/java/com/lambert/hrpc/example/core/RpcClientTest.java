package com.lambert.hrpc.example.core;

import com.lambert.hrpc.RpcContext;
import com.lambert.hrpc.RpcProxyFactory;
import com.lambert.hrpc.RpcServer;
import com.lambert.hrpc.example.HelloService;
import com.lambert.hrpc.registry.ServiceRegistry;
import com.lambert.hrpc.registry.zookeeper.ZookeeperServiceRegistry;

/**
 * Created by pc on 2015/12/11.
 */
public class RpcClientTest {



    public static void testClient(){

        RpcContext context = new RpcContext();
        
        RpcProxyFactory factory = new RpcProxyFactory(context);
        HelloService helloService = factory.create(HelloService.class);
        String result = helloService.hello("World");

        System.out.println(result);
    }

    public static void main(String[] args) throws Exception{

        testClient();
    }
}
