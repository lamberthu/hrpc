package com.lambert.hrpc.example;

import com.lambert.hrpc.core.RpcProxyFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

public class ConsumerTest {

    @Autowired
    private RpcProxyFactory rpcProxy;

    @Test
    public void helloTest() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        Assert.assertEquals("Hello! World", result);
    }

}
