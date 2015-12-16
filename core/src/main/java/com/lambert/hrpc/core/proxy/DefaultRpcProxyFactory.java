package com.lambert.hrpc.core.proxy;

import com.lambert.hrpc.core.RpcContext;
import com.lambert.hrpc.core.RpcProxyFactory;
import com.lambert.hrpc.core.pojo.Request;
import com.lambert.hrpc.core.pojo.Response;
import com.lambert.hrpc.core.registry.ServiceRegistry;
import com.lambert.hrpc.core.serialization.Serializer;
import com.lambert.hrpc.core.transportation.Sender;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by pc on 2015/12/15.
 */
public class DefaultRpcProxyFactory implements RpcProxyFactory {
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    private Sender sender;
    private RpcContext context;

    public DefaultRpcProxyFactory(RpcContext context){
        this.context = context;
        this.serviceRegistry = context.getServiceRegistry();
        this.sender = context.getSender();
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
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        request.setServerHost(host);
                        request.setServerPort(port);

                        Response response = sender.send(request);
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
