package com.lambert.hrpc.transportation.netty;

import com.lambert.hrpc.pojo.Request;
import com.lambert.hrpc.pojo.Response;
import com.lambert.hrpc.transportation.ReceiveServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 2015/12/10.
 */
public class NettyReceiveServer implements ReceiveServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyReceiveServer.class);

    private String serverAddress;

    private Map<String, Object> handlerMap = new HashMap<>();

    public NettyReceiveServer(){}

    public NettyReceiveServer(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void start() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new Decoder(Request.class)) // 将 RPC 请求进行解码（为了处理请求）
                                    .addLast(new Encoder(Response.class)) // 将 RPC 响应进行编码（为了返回响应）
                                    .addLast(new NettyHandler(handlerMap)); // 处理 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            LOGGER.debug("server started on port {}", port);


            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
