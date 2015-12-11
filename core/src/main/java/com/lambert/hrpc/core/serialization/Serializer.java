package com.lambert.hrpc.core.serialization;

/**
 * 序列化器
 * Created by pc on 2015/12/10.
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data , Class<T> cls);
}
