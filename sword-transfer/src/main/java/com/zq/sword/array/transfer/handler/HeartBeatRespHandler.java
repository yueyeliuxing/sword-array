package com.zq.sword.array.transfer.handler;

import com.zq.sword.array.transfer.message.Header;
import com.zq.sword.array.transfer.message.MessageType;
import com.zq.sword.array.transfer.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: sword-array
 * @description: 心跳返回消息处理器
 * @author: zhouqi1
 * @create: 2018-07-06 15:07
 **/
public class HeartBeatRespHandler extends TransferHandler {
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
        TransferMessage message = (TransferMessage)msg;
        if(message != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            System.out.println("Receive client heart beat message : --> " + message);
            TransferMessage heartBeat = buildHeartBeat();
            System.out.println("Send heart beat response message to client : -->" + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildHeartBeat() {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }
}
