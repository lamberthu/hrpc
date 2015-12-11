package com.lambert.hrpc.transportation.netty;

import com.lambert.hrpc.pojo.Request;
import com.lambert.hrpc.pojo.Response;
import com.lambert.hrpc.RpcContext;
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
 * Netty 服务用于接收Client的网络连接
 *
 * Created by pc on 2015/12/10.
 */
public class NettyReceiveServer implements ReceiveServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyReceiveServer.class);

    private String serverAddress;

    private Map<String, Object> handlerMap = new HashMap<>();

    private RpcContext context;

    public NettyReceiveServer(){}

    public NettyReceiveServer(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }
    public NettyReceiveServer(RpcContext context){
        this.context = context;
        this.serverAddress = context.getConf().getServiceAddress();
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
                                    .addLast(new Decoder(Request.class , context.getSerializer()))
                                    .addLast(new Encoder(Response.class , context.getSerializer()))
                                    .addLast(new NettyHandler(handlerMap));
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

    public void setHandlerMap(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void setContext(RpcContext context) {
        this.context = context;
    }
}
