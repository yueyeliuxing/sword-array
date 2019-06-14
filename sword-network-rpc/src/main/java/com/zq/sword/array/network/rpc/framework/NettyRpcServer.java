package com.zq.sword.array.network.rpc.framework;

import com.zq.sword.array.network.rpc.framework.protocol.message.Header;
import com.zq.sword.array.network.rpc.framework.protocol.message.MessageType;
import com.zq.sword.array.network.rpc.framework.protocol.message.TransferMessage;
import com.zq.sword.array.utils.IPUtil;
import com.zq.sword.array.network.rpc.framework.protocol.coder.NettyMessageDecoder;
import com.zq.sword.array.network.rpc.framework.protocol.coder.NettyMessageEncoder;
import com.zq.sword.array.network.rpc.framework.protocol.handler.HeartBeatRespHandler;
import com.zq.sword.array.network.rpc.framework.protocol.handler.LoginAuthRespHandler;
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: piper 服务
 * @author: zhouqi1
 * @create: 2018-07-02 19:42
 **/
public class NettyRpcServer implements RpcServer {

    private Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    public static final String SERVICE_NAME = "service.interface.name";
    public static final String SERVICE_METHOD = "service.interface.method";

    /**
     * Service 集合
     */
    private Map<String, Object> services;

    private int port;

    private ServerBootstrap bootstrap;

    private volatile boolean started = false;

    public NettyRpcServer(int port) {
        this.port = port;
        services = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) throws Exception{
        new NettyRpcServer(6440).start();
    }

    @Override
    public void start() {
        try{
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(10);
            EventLoopGroup businessGroup = new NioEventLoopGroup(10);
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
                                    .addLast("HeartBeatHandler", new HeartBeatRespHandler())
                                    .addLast(businessGroup, new NettyRpcServer.ProtocolHandler());
                        }
                    });
            logger.info("Netty server start ok :" + port);
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
    public void registerService(Object service) {
        Class<?>[] interfaces =  service.getClass().getInterfaces();
        if(interfaces != null){
            for (Class<?> inter : interfaces){
                services.put(inter.getName(), service);
            }
        }else {
            services.put(service.getClass().getName(), service);
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean started() {
        return started;
    }

    private class ProtocolHandler extends ChannelHandlerAdapter {

        private Logger logger = LoggerFactory.getLogger(ProtocolHandler.class);

        public ProtocolHandler() {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            TransferMessage message = (TransferMessage) msg;
            logger.info("receive msg request : {}", message);
            if (message.getHeader() != null && message.getHeader().getType() == MessageType.BUSINESS_REQ.value()) {
                Map<String, Object> attachment = message.getHeader().getAttachment();
                String serviceName = attachment.get(SERVICE_NAME).toString();
                String methodName = attachment.get(SERVICE_METHOD).toString();
                Object[] params = (Object[]) message.getBody();
                Object service = services.get(serviceName);
                Class<?>[] paramTypes = null;
                if (params != null) {
                    params = new Class[params.length];
                    for (int i = 0; i < params.length; i++) {
                        paramTypes[i] = params.getClass();
                    }
                }
                Method method = service.getClass().getMethod(methodName, paramTypes);
                if (method == null) {
                    return;
                }
                method.setAccessible(true);
                Object result = method.invoke(service, params);
                if(result != null){
                    ctx.writeAndFlush(buildBusinessResp(serviceName, methodName, result));
                }
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    /**
     * 构建业务返回值
     * @param serviceName
     * @param method
     * @param result
     * @return
     */
    private TransferMessage buildBusinessResp(String serviceName, String method, Object result) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.BUSINESS_RESP.value());
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(SERVICE_NAME, serviceName);
        attachment.put(SERVICE_METHOD, method);
        header.setAttachment(attachment);
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

}
