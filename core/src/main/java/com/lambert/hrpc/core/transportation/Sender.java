package com.lambert.hrpc.core.transportation;

import com.lambert.hrpc.core.pojo.Response;
import com.lambert.hrpc.core.pojo.Request;

/**
 * 向服务器发送数据
 * Created by pc on 2015/12/10.
 */
public interface Sender {

    Response send(Request request) throws Exception;

}
