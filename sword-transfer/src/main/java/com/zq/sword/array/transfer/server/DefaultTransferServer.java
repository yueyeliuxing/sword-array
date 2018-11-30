package com.zq.sword.array.transfer.server;

import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.transfer.coder.NettyMessageDecoder;
import com.zq.sword.array.transfer.coder.NettyMessageEncoder;
import com.zq.sword.array.transfer.handler.HeartBeatRespHandler;
import com.zq.sword.array.transfer.handler.LoginAuthRespHandler;
import com.zq.sword.array.transfer.handler.TransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * @program: sword-array
 * @description: piper 服务
 * @author: zhouqi1
 * @create: 2018-07-02 19:42
 **/
public class DefaultTransferServer implements TransferServer {

    private Logger logger = LoggerFactory.getLogger(DefaultTransferServer.class);

    private int port;

    private List<TransferHandler> transferHandlers;

    private ServerBootstrap bootstrap;

    private volatile boolean started = false;

    public DefaultTransferServer(int port) {
        this.port = port;
        transferHandlers = new CopyOnWriteArrayList<>();
    }

    public static void main(String[] args) throws Exception{
        new DefaultTransferServer(6440).start();
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
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyMessageDecoder(Integer.MAX_VALUE, 4, 4))
                                   .addLast(new NettyMessageEncoder())
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                    .addLast(new LoginAuthRespHandler())
                                    .addLast("HeartBeatHandler", new HeartBeatRespHandler());
                            if(transferHandlers != null && !transferHandlers.isEmpty()){
                                transferHandlers.forEach(transferHandler -> {
                                    pipeline.addLast(transferHandler);
                                });
                            }
                        }
                    });
            System.out.println("Netty server start ok :" + port);
            ChannelFuture f = bootstrap.bind(IPUtil.getServerIp(),port)
                    .addListener(new GenericFutureListener(){

                        @Override
                        public void operationComplete(io.netty.util.concurrent.Future future) throws Exception {
                            started = true;
                        }
                    }).sync();
            f.channel().closeFuture().sync();
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

    @Override
    public boolean started() {
        return started;
    }


}
