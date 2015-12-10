package com.lambert.hrpc;

import com.lambert.hrpc.annotation.RpcService;

@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}