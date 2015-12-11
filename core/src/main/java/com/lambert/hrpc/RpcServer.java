package com.lambert.hrpc;

import com.lambert.hrpc.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RpcServer  {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

//    private ServiceRegistry serviceRegistry;
//    private ReceiveServer receiveServer;
    private RpcContext context ;

    private Map<String, Object> handlerMap = new HashMap<>(); // 存放接口名与服务对象之间的映射关系


    public RpcServer(RpcContext context) {
        this.context = context;
    }

//    @Override
//    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
//        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class); // 获取所有带有 RpcService 注解的 Spring Bean
//        if (MapUtils.isNotEmpty(serviceBeanMap)) {
//            for (Object serviceBean : serviceBeanMap.values()) {
//                addService(serviceBean);
//            }
//        }
//    }


//    @Override
//    public void afterPropertiesSet() throws Exception {
//        String localIp = NetUtils.getMachineIp();
//        serverAddress = String.format("%s:%s", localIp, servicePort);
//        receiveServer.start();
//    }

    public void start() throws Exception{
        context.getReceiveServer().setHandlerMap(handlerMap);
        context.getReceiveServer().start();
        LOGGER.info("Service started ... ");
    }

    /**
     * 添加服务
     * @param serviceBean
     */
    public void addService(Object serviceBean) {
        String serviceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
        handlerMap.put(serviceName, serviceBean);
        context.getServiceRegistry().register(serviceName, context.getConf().getServiceAddress());
        LOGGER.info("add service {}" , serviceName );
    }
}