package com.lambert.hrpc.core.handler;

import com.lambert.hrpc.core.pojo.Request;
import com.lambert.hrpc.core.pojo.Response;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Map;

/**
 * Created lambert pc on 2015/12/14.
 */
public class FastHandler implements Handler {

    private Map<String , Object> objectMap ;

    public FastHandler(Map<String,Object> objectMap){
        this.objectMap = objectMap;
    }
    public FastHandler(){}

    @Override
    public Response handle(Request request) {
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        try {

            String className = request.getClassName();
            Object serviceBean = objectMap.get(className);

            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
            Object result =  serviceFastMethod.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }
        return response;
    }

    public void setObjectMap(Map<String, Object> objectMap) {
        this.objectMap = objectMap;
    }
}
