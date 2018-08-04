package com.zq.sword.array.netty.server;

import com.zq.sword.array.netty.coder.NettyMessageDecoder;
import com.zq.sword.array.netty.coder.NettyMessageEncoder;
import com.zq.sword.array.netty.handler.HeartBeatRespHandler;
import com.zq.sword.array.netty.handler.LoginAuthRespHandler;
import com.zq.sword.array.netty.handler.TransferHandler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: piper 服务
 * @author: zhouqi1
 * @create: 2018-07-02 19:42
 **/
public class DefaultTransferServer implements TransferServer {

    private Logger logger = LoggerFactory.getLogger(DefaultTransferServer.class);

    private String host;

    private int port;

    private List<TransferHandler> transferHandlers;

    public DefaultTransferServer(String host, int port) {
        this.host = host;
        this.port = port;
        transferHandlers = new CopyOnWriteArrayList<>();
    }

    public static void main(String[] args) throws Exception{
        new DefaultTransferServer("127.0.0.1", 6440).start();
    }

    @Override
    public void registerTransferHandler(TransferHandler transferHandler) {
        transferHandlers.add(transferHandler);
    }

    @Override
    public void start() {
        try{
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
        }catch (Exception e){
            logger.error("start error", e);
        }

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void restart() {
        start();
    }
}
