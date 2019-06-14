package com.zq.sword.array.network.rpc.framework;

import com.zq.sword.array.network.rpc.framework.protocol.coder.NettyMessageDecoder;
import com.zq.sword.array.network.rpc.framework.protocol.coder.NettyMessageEncoder;
import com.zq.sword.array.network.rpc.framework.protocol.handler.HeartBeatReqHandler;
import com.zq.sword.array.network.rpc.framework.protocol.handler.LoginAuthReqHandler;
import com.zq.sword.array.network.rpc.framework.protocol.message.Header;
import com.zq.sword.array.network.rpc.framework.protocol.message.MessageType;
import com.zq.sword.array.network.rpc.framework.protocol.message.TransferMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import static com.zq.sword.array.network.rpc.framework.NettyRpcServer.SERVICE_METHOD;
import static com.zq.sword.array.network.rpc.framework.NettyRpcServer.SERVICE_NAME;

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

    private Map<String, SynchronousQueue<Object>> resultQueueSet;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        resultQueueSet = new HashMap<>();
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
                                    .addLast(new ProtocolHandler());
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
    public Object getProxy(Class<?> serviceInterface) {
        return Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(!isClose()){
                    TransferMessage transferMessage = buildBusinessReq(serviceInterface.getName(), method.getName(), args);
                    channel.writeAndFlush(transferMessage);
                    synchronized (resultQueueSet){
                        SynchronousQueue<Object> resultQueue = resultQueueSet.get(buildServiceKey(serviceInterface.getName(), method.getName()));
                        if(resultQueue == null){
                            resultQueue = new SynchronousQueue<>();
                        }
                        return resultQueue.poll();
                    }
                }
                return null;
            }
        });
    }

    private String buildServiceKey(String serviceName, String method) {
        return String.format("%s-%s", serviceName, method);
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

    /**
     * 构建业务请求
     * @param serviceName
     * @param method
     * @param params
     * @return
     */
    private TransferMessage buildBusinessReq(String serviceName, String method, Object[] params) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.BUSINESS_REQ.value());
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(SERVICE_NAME, serviceName);
        attachment.put(SERVICE_METHOD, method);
        header.setAttachment(attachment);
        message.setHeader(header);
        message.setBody(params);
        return message;
    }

    public class ProtocolHandler extends ChannelHandlerAdapter {

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
            if (message.getHeader() != null && message.getHeader().getType() == MessageType.BUSINESS_RESP.value()) {
                Map<String, Object> attachment = message.getHeader().getAttachment();
                String serviceName = attachment.get(SERVICE_NAME).toString();
                String methodName = attachment.get(SERVICE_METHOD).toString();
                Object result = message.getBody();
                synchronized (resultQueueSet){
                    SynchronousQueue<Object> resultQueue = resultQueueSet.get(buildServiceKey(serviceName, methodName));
                    if(resultQueue != null){
                        resultQueue.add(result);
                    }
                }
            }else {
                ctx.fireChannelRead(msg);
            }
        }
    }
}
