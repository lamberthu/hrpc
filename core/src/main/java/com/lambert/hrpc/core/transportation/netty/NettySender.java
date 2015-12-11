package com.lambert.hrpc.core.transportation.netty;

import com.lambert.hrpc.core.RpcContext;
import com.lambert.hrpc.core.pojo.Response;
import com.lambert.hrpc.core.transportation.Sender;
import com.lambert.hrpc.core.pojo.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettySender extends SimpleChannelInboundHandler<Response> implements Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettySender.class);

    private String host;
    private int port;

    private Response response;

    private RpcContext context ;

    private final Object obj = new Object();

    public NettySender(String host, int port , RpcContext context) {
        this.host = host;
        this.port = port;
        this.context = context;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        this.response = response;

        synchronized (obj) {
            obj.notifyAll(); // 收到响应，唤醒线程
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }

    @Override
    public Response send(Request request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                            .addLast(new Encoder(Request.class , context.getSerializer())) // 将 RPC 请求进行编码（为了发送请求）
                            .addLast(new Decoder(Response.class , context.getSerializer())) // 将 RPC 响应进行解码（为了处理响应）
                            .addLast(NettySender.this);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            synchronized (obj) {
                obj.wait(); // 未收到响应，使线程等待
            }

            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}