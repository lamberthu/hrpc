package com.lambert.hrpc.core.transportation.netty;

import com.lambert.hrpc.core.handler.Handler;
import com.lambert.hrpc.core.pojo.Response;
import com.lambert.hrpc.core.pojo.Request;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NettyHandler extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyHandler.class);

    private Handler handler;

    public NettyHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Request request) throws Exception {
        Response response = handler.handle(request);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}