package com.lambert.hrpc.core;

import com.lambert.hrpc.core.handler.FastHandler;
import com.lambert.hrpc.core.handler.Handler;
import com.lambert.hrpc.core.serialization.Serializer;
import com.lambert.hrpc.core.serialization.protostuff.ProtostuffSerializer;
import com.lambert.hrpc.core.registry.ServiceRegistry;
import com.lambert.hrpc.core.registry.zookeeper.ZookeeperServiceRegistry;
import com.lambert.hrpc.core.transportation.ReceiveServer;
import com.lambert.hrpc.core.transportation.netty.NettyReceiveServer;

/**
 * RPC 运行上下文
 * Created by pc on 2015/12/11.
 */
public class RpcContext {

    private Serializer serializer;
    private ServiceRegistry serviceRegistry;
    private ReceiveServer receiveServer;
    private Handler handler;

    private RpcConf conf ;



    public void initComponent(){
        // init default component

        if(conf == null) {
            conf = new RpcConf();
        }

        this.serializer = new ProtostuffSerializer();

        this.serviceRegistry = new ZookeeperServiceRegistry(conf);

        this.handler = new FastHandler();

        this.receiveServer = new NettyReceiveServer(this);
    }

    public RpcContext(){
        initComponent();
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public ReceiveServer getReceiveServer() {
        return receiveServer;
    }

    public void setReceiveServer(ReceiveServer receiveServer) {
        this.receiveServer = receiveServer;
        this.receiveServer.setContext(this);
    }

    public RpcConf getConf() {
        return conf;
    }

    public void setConf(RpcConf conf) {
        this.conf = conf;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
