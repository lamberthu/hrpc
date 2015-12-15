package com.lambert.hrpc.core;

import com.lambert.hrpc.core.handler.FastHandler;
import com.lambert.hrpc.core.handler.Handler;
import com.lambert.hrpc.core.serialization.Serializer;
import com.lambert.hrpc.core.serialization.protostuff.ProtostuffSerializer;
import com.lambert.hrpc.core.registry.ServiceRegistry;
import com.lambert.hrpc.core.registry.zookeeper.ZookeeperServiceRegistry;
import com.lambert.hrpc.core.transportation.ReceiveServer;
import com.lambert.hrpc.core.transportation.Sender;
import com.lambert.hrpc.core.transportation.netty.NettyReceiveServer;
import com.lambert.hrpc.core.transportation.netty.NettySender;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RPC 运行上下文
 * Created by pc on 2015/12/11.
 */
public class RpcContext {

    private AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * common
     */

    private Serializer serializer;
    private ServiceRegistry serviceRegistry;

    /**
     * server
     */
    private ReceiveServer receiveServer;
    private Handler handler;

    /**
     * client
     */

    private Sender sender;


    /**
     * 初始化组件
     */
    public void initComponent(){
        // init default component

        if(!isInit.get()) {
            synchronized (this) {
                if(!isInit.get()) {

                    if(this.serializer == null ) {
                        this.serializer = new ProtostuffSerializer();
                    }
                    if(this.serviceRegistry == null) {
                        this.serviceRegistry = new ZookeeperServiceRegistry();
                    }
                    if(this.handler == null) {
                        this.handler = new FastHandler();
                    }
                    if(this.receiveServer == null) {
                        this.receiveServer = new NettyReceiveServer(this);
                    }
                    if (this.sender == null ){
                        this.sender = new NettySender(this.getSerializer());
                    }
                }
            }
        }
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


    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
