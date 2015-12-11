package com.lambert.hrpc;

import com.lambert.hrpc.transportation.netty.NettySender;
import com.lambert.hrpc.pojo.Request;
import com.lambert.hrpc.pojo.Response;
import com.lambert.hrpc.registry.ServiceRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcProxyFactory {

    private String serverAddress;
    private ServiceRegistry serviceRegistry;

    private RpcContext context;

    public RpcProxyFactory(RpcContext context){
        this.context = context;
        this.serviceRegistry = context.getServiceRegistry();
    }


    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request request = new Request(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceRegistry != null) {
                            serverAddress = serviceRegistry.lookup(request.getClassName()); // 发现服务
                        }
                        System.out.println(serverAddress);
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        NettySender client = new NettySender(host, port , context); // 初始化 RPC 客户端
                        Response response = client.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}