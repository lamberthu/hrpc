package com.lambert.hrpc.server;

import com.lambert.hrpc.annotation.RpcService;
import com.lambert.hrpc.registry.ServiceRegistry;
import com.lambert.hrpc.transportation.ReceiveServer;
import com.lambert.hrpc.utils.NetUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private ServiceRegistry serviceRegistry;
    private ReceiveServer receiveServer;
    private String serverAddress;
    private String servicePort;

    private Map<String, Object> handlerMap = new HashMap<>(); // 存放接口名与服务对象之间的映射关系


    public RpcServer(String servicePort , ServiceRegistry serviceRegistry) {
        this.servicePort = servicePort;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class); // 获取所有带有 RpcService 注解的 Spring Bean
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                addService(serviceBean);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        String localIp = NetUtils.getMachineIp();
        serverAddress = String.format("%s:%s", localIp, servicePort);
        receiveServer.start();
    }

    /**
     * 添加服务
     * @param serviceBean
     */
    public void addService(Object serviceBean) {
        String serviceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
        handlerMap.put(serviceName, serviceBean);
        serviceRegistry.register(serviceName, serverAddress);
    }
}