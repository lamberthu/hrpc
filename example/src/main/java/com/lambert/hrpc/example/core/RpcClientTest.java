package com.lambert.hrpc.example.core;

import com.lambert.hrpc.core.RpcContext;
import com.lambert.hrpc.core.RpcProxyFactory;
import com.lambert.hrpc.core.proxy.DefaultRpcProxyFactory;
import com.lambert.hrpc.example.HelloService;

/**
 * Created by pc on 2015/12/11.
 */
public class RpcClientTest {



    public static void testClient(){

        RpcContext context = new RpcContext();

        RpcProxyFactory factory = new DefaultRpcProxyFactory(context);
        HelloService helloService = factory.create(HelloService.class);

        for(int i = 0 ; i < 10 ; i++) {
            long start = System.currentTimeMillis();
            String result = helloService.hello("World");
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            System.out.println(result);
        }
    }

    public static void main(String[] args) throws Exception{

        testClient();

    }
}
