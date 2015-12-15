package com.lambert.hrpc.core;

/**
 * Client端的代理工厂类
 */
public interface RpcProxyFactory {

    /**
     * 创建一个代理
     * @param interfaceClass
     * @param <T>
     * @return
     */
    <T> T create(Class<?> interfaceClass);

}