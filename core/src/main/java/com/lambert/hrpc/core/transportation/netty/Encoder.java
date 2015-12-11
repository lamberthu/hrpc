package com.lambert.hrpc.core.transportation.netty;

import com.lambert.hrpc.core.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty 编码器
 */
public class Encoder extends MessageToByteEncoder {

    private Class<?> genericClass;
    private Serializer serializer;

    public Encoder(Class<?> genericClass , Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}