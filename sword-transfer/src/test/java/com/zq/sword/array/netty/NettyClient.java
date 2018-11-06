package com.zq.sword.array.netty;

import com.zq.sword.array.netty.coder.NettyMessageDecoder;
import com.zq.sword.array.netty.coder.NettyMessageEncoder;
import com.zq.sword.array.netty.handler.HeartBeatReqHandler;
import com.zq.sword.array.netty.handler.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: redis-sync
 * @description: piper 服务
 * @author: zhouqi1
 * @create: 2018-07-02 19:42
 **/
public class NettyClient {

    private String host;

    private int port;

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup();

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void  connect() throws Exception {
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageDecoder(1024*1024, 4, 4))
                                    .addLast(new MessageEncoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter(){
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                   // ByteBuf sendBuf = Unpooled.buffer();
                                   // sendBuf.writeInt(1);
                                    //ctx.writeAndFlush(sendBuf);
                                    ctx.writeAndFlush("TYS");
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    /*ByteBuf byteBuf = (ByteBuf) msg;
                                    int v = byteBuf.readInt();
                                    if(v==2){
                                        System.out.println("你好收到了2");
                                    }else {
                                        System.out.println("你好不是2呀");
                                    }*/
                                    String str = (String)msg;
                                    if("TYT".endsWith(str)){
                                        System.out.println("你好收到了TYT");
                                    }else {
                                        System.out.println("你好不是TYT呀");
                                    }
                                }
                            });
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
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

    public static void main(String[] args) throws Exception{
        System.out.println("连接redis...");
        new NettyClient("127.0.0.1", 6440).connect();
        System.in.read();
    }
}
