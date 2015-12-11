package com.lambert.hrpc.transportation.netty;

import com.lambert.hrpc.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Netty 解码器
 */

public class Decoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    private Serializer serializer ;
    public Decoder(Class<?> genericClass , Serializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }


    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = serializer.deserialize(data, genericClass);
        out.add(obj);
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}