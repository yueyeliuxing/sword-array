package com.zq.sword.array.netty;

import com.zq.sword.array.netty.coder.NettyMessageDecoder;
import com.zq.sword.array.netty.coder.NettyMessageEncoder;
import com.zq.sword.array.netty.handler.HeartBeatRespHandler;
import com.zq.sword.array.netty.handler.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @program: redis-sync
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
                        ch.pipeline().addLast(new MessageDecoder(1024*1024, 4, 4))
                                .addLast(new MessageEncoder());
                        ch.pipeline().addLast(new ChannelHandlerAdapter(){
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                               /* ByteBuf byteBuf = (ByteBuf) msg;
                                int v = byteBuf.readInt();
                                if(v==1){
                                    System.out.println("你好收到了1");
                                    byteBuf.clear();
                                    byteBuf.writeInt(2);
                                    ctx.writeAndFlush(byteBuf);
                                }else {
                                    System.out.println("你好不是1呀");
                                    byteBuf.clear();
                                    byteBuf.writeInt(3);
                                    ctx.writeAndFlush(byteBuf);
                                }*/

                                String str = (String)msg;
                                if("TYS".endsWith(str)){
                                    System.out.println("你好收到了TYS");
                                    ctx.writeAndFlush("TYT");
                                }else {
                                    System.out.println("你好不是TYS呀");
                                    ctx.writeAndFlush("TYR");
                                }
                            }
                        });
                    }
                });
        System.out.println("Netty server start ok :" + host + ":" + port);
        ChannelFuture f = b.bind(host, port).sync();
        f.channel().closeFuture().sync();

    }

    public static void main(String[] args) throws Exception{
        new NettyServer("127.0.0.1", 6440).bind();
    }
}
