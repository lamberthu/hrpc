package com.lambert.hrpc.registry;

/**
 * 服务注册中心
 * Created by pc on 2015/12/9.
 */
public interface ServiceRegistry {

    /**
     *  注册服务
     * @param serviceName  eg. com.lambert.service.EchoService
     * @param address  eg. 127.0.0.1:8000
     */
    void register(String serviceName,String address);

    /**
     * 发现服务 ， 获得一个服务调用地址
     * @param serviceName eg. com.lambert.service.EchoService
     * @return  eg. 127.0.0.1:8000
     */
    String lookup(String serviceName);


}
