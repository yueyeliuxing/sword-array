package com.zq.sword.array.network.rpc.client;

import com.zq.sword.array.network.rpc.coder.NettyMessageDecoder;
import com.zq.sword.array.network.rpc.coder.NettyMessageEncoder;
import com.zq.sword.array.network.rpc.handler.HeartBeatReqHandler;
import com.zq.sword.array.network.rpc.handler.LoginAuthReqHandler;
import com.zq.sword.array.network.rpc.handler.TransferHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 默认的传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:02
 **/
public class NettyRpcClient implements RpcClient {

    private Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    private String host;

    private int port;

    private List<TransferHandler> transferHandlers;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        transferHandlers = new ArrayList<>();
    }

    @Override
    public void registerTransferHandler(TransferHandler transferHandler) {
        transferHandlers.add(transferHandler);
    }

    @Override
    public void connect() {
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyMessageDecoder(Integer.MAX_VALUE, 4, 4))
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
            logger.info("连接服务：{}:{}", host, port);
            ChannelFuture future = b.connect(host, port).sync();

            channel = future.channel();
            channel.closeFuture().sync();
        }catch (Exception e){
            group.shutdownGracefully();
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
    public void disconnect() {
        if(channel != null && channel.isOpen()){
            channel.close();
        }
        if(group != null){
            group.shutdownGracefully();
        }
    }

    @Override
    public void reconnect() {
        connect();
    }
}
