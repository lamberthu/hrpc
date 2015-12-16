package com.lambert.hrpc.core.transportation.netty;

import com.lambert.hrpc.core.RpcConf;
import com.lambert.hrpc.core.RpcContext;
import com.lambert.hrpc.core.handler.Handler;
import com.lambert.hrpc.core.pojo.Request;
import com.lambert.hrpc.core.pojo.Response;
import com.lambert.hrpc.core.serialization.Serializer;
import com.lambert.hrpc.core.transportation.ReceiveServer;
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

/**
 * Netty 服务用于接收Client的网络连接
 *
 * Created by pc on 2015/12/10.
 */
public class NettyReceiveServer implements ReceiveServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyReceiveServer.class);

    private String serverAddress;

    private RpcContext context;

    private Handler handler ;

    private Serializer serializer;

    public NettyReceiveServer(){
        this.serverAddress = RpcConf.getINSTANCE().getServiceAddress();
    }

    public NettyReceiveServer(RpcContext context){
        this();
        this.context = context;

        this.handler = context.getHandler();
        this.serializer = context.getSerializer();
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
                                    .addLast(new Decoder(Request.class , serializer))
                                    .addLast(new Encoder(Response.class , serializer))
                                    .addLast(new NettyHandler(handler));
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

    public void setContext(RpcContext context) {
        this.context = context;
    }
}
