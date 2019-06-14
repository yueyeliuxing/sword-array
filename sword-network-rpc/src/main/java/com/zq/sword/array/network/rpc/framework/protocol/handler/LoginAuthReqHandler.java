package com.zq.sword.array.network.rpc.framework.protocol.handler;

import com.zq.sword.array.network.rpc.framework.protocol.message.TransferMessage;
import com.zq.sword.array.network.rpc.framework.protocol.message.Header;
import com.zq.sword.array.network.rpc.framework.protocol.message.MessageType;
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
       TransferMessage message = (TransferMessage)msg;

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

    private TransferMessage buildLogReq() {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }
}
