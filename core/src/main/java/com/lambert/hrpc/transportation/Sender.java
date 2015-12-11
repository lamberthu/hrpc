package com.lambert.hrpc.transportation;

import com.lambert.hrpc.pojo.Request;
import com.lambert.hrpc.pojo.Response;

/**
 * 向服务器发送数据
 * Created by pc on 2015/12/10.
 */
public interface Sender {

    Response send(Request request) throws Exception;

}
