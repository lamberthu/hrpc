package com.lambert.hrpc.core.handler;

import com.lambert.hrpc.core.pojo.Request;
import com.lambert.hrpc.core.pojo.Response;

import java.util.Map;

/**
 * Created by pc on 2015/12/14.
 */
public interface Handler {

    Response handle(Request request);
    void setObjectMap(Map<String, Object> objectMap);
}
