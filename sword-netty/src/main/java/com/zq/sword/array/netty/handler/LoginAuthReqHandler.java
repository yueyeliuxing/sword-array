package com.zq.sword.array.netty.handler;

import com.zq.redis.piper.netty.message.Header;
import com.zq.redis.piper.netty.message.MessageType;
import com.zq.redis.piper.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: sword-array
 * @description: 登陆验证
 * @author: zhouqi1
 * @create: 2018-07-06 13:52
 **/
public class LoginAuthReqHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLogReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       NettyMessage message = (NettyMessage)msg;

       if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
           byte loginResult = (byte)message.getBody();
           if(loginResult != (byte)0) {
                ctx.close();
           }else {
               System.out.println("login is ok : " + message);
               ctx.fireChannelRead(msg);
           }
       }else {
           ctx.fireChannelRead(msg);
       }
    }

    private NettyMessage buildLogReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }
}
