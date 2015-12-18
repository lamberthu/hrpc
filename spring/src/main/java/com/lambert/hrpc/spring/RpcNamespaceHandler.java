package com.lambert.hrpc.spring;

import com.lambert.hrpc.core.RpcServer;
import com.lambert.hrpc.core.handler.FastHandler;
import com.lambert.hrpc.core.proxy.DefaultRpcProxyFactory;
import com.lambert.hrpc.core.registry.ServiceRegistry;
import com.lambert.hrpc.core.registry.zookeeper.ZookeeperServiceRegistry;
import com.lambert.hrpc.core.serialization.protostuff.ProtostuffSerializer;
import com.lambert.hrpc.core.transportation.netty.NettyReceiveServer;
import com.lambert.hrpc.core.transportation.netty.NettySender;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by pc on 2015/12/16.
 */
public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("registry",new RpcBeanDefinitionParser(ZookeeperServiceRegistry.class));
        registerBeanDefinitionParser("serialization",new RpcBeanDefinitionParser(ProtostuffSerializer.class));
        registerBeanDefinitionParser("handler",new RpcBeanDefinitionParser(FastHandler.class));
        registerBeanDefinitionParser("receiveServer",new RpcBeanDefinitionParser(NettyReceiveServer.class));
        registerBeanDefinitionParser("sender",new RpcBeanDefinitionParser(NettySender.class));
        registerBeanDefinitionParser("rpcServer",new RpcBeanDefinitionParser(RpcServer.class));
        registerBeanDefinitionParser("rpcProxyFactory",new RpcBeanDefinitionParser(DefaultRpcProxyFactory.class));
        registerBeanDefinitionParser("service",new RpcBeanDefinitionParser(Object.class));
        registerBeanDefinitionParser("reference",new RpcBeanDefinitionParser(Object.class));

    }
}
