package com.lambert.hrpc.core.example;

import com.lambert.hrpc.core.annotation.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}