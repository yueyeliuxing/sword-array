package com.zq.sword.array.netty.client;

import com.zq.sword.array.netty.coder.NettyMessageDecoder;
import com.zq.sword.array.netty.coder.NettyMessageEncoder;
import com.zq.sword.array.netty.handler.HeartBeatReqHandler;
import com.zq.sword.array.netty.handler.LoginAuthReqHandler;
import com.zq.sword.array.netty.handler.TransferHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 默认的传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:02
 **/
public class DefaultTransferClient implements TransferClient {

    private Logger logger = LoggerFactory.getLogger(DefaultTransferClient.class);

    private String host;

    private int port;

    private List<TransferHandler> transferHandlers;

    private EventLoopGroup group = new NioEventLoopGroup();

    public DefaultTransferClient(String host, int port) {
        this.host = host;
        this.port = port;
        transferHandlers = new ArrayList<>();
    }


    public void connect() {
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4, 4))
                                    .addLast("MessageEncoder", new NettyMessageEncoder())
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                    .addLast("LoginAuthHandler", new LoginAuthReqHandler())
                                    .addLast("HeartBeatHandler", new HeartBeatReqHandler());

                            if(transferHandlers != null && !transferHandlers.isEmpty()){
                                transferHandlers.forEach(transferHandler -> {
                                    pipeline.addLast(transferHandler);
                                });
                            }
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            logger.error("sync error", e);
        }finally {
            group.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        TimeUnit.SECONDS.sleep(5);
                        try{
                            connect();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void registerTransferHandler(TransferHandler transferHandler) {
        transferHandlers.add(transferHandler);
    }

    @Override
    public void start() {
        connect();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void restart() {

    }
}
