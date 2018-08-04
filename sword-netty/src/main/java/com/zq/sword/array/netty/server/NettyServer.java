package com.zq.sword.array.netty.server;

import com.zq.sword.array.netty.coder.NettyMessageDecoder;
import com.zq.sword.array.netty.coder.NettyMessageEncoder;
import com.zq.sword.array.netty.handler.HeartBeatRespHandler;
import com.zq.sword.array.netty.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @program: sword-array
 * @description: piper 服务
 * @author: zhouqi1
 * @create: 2018-07-02 19:42
 **/
public class NettyServer {

    private String host;

    private int port;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void bind() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4, 4))
                                .addLast(new NettyMessageEncoder())
                                .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                .addLast(new LoginAuthRespHandler())
                                .addLast("HeartBeatHandler", new HeartBeatRespHandler());
                    }
                });
        b.bind(host, port).sync();
        System.out.println("Netty server start ok :" + host + ":" + port);
    }

    public static void main(String[] args) throws Exception{
        new NettyServer("127.0.0.1", 6440).bind();
    }
}
