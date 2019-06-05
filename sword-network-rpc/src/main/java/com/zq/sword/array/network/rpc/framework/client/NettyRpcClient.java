package com.zq.sword.array.network.rpc.framework.client;

import com.zq.sword.array.network.rpc.framework.coder.NettyMessageDecoder;
import com.zq.sword.array.network.rpc.framework.coder.NettyMessageEncoder;
import com.zq.sword.array.network.rpc.framework.handler.HeartBeatReqHandler;
import com.zq.sword.array.network.rpc.framework.handler.LoginAuthReqHandler;
import com.zq.sword.array.network.rpc.framework.handler.ProtocolHandler;
import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private List<ProtocolProcessor> protocolProcessors;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        protocolProcessors = new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerProtocolProcessor(ProtocolProcessor protocolProcessor) {
        protocolProcessors.add(protocolProcessor);
    }

    @Override
    public void start() {
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
                                    .addLast("HeartBeatHandler", new HeartBeatReqHandler())
                                    .addLast(new ProtocolHandler(protocolProcessors));
                        }
                    });
            logger.info("连接服务：{}:{}", host, port);
            ChannelFuture future = b.connect(host, port).sync();
            channel = future.channel();
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
                            start();
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
    public void write(Object msg) {
        if(!isClose()){
            channel.writeAndFlush(msg);
        }
    }

    @Override
    public void close() {
        if(channel != null && channel.isOpen()){
            channel.close();
        }
        if(group != null){
            group.shutdownGracefully();
        }
    }

    @Override
    public boolean isClose() {
        return channel == null || !channel.isOpen();
    }
}
