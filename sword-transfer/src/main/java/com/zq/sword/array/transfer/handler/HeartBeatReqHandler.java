package com.zq.sword.array.transfer.handler;

import com.zq.sword.array.transfer.message.Header;
import com.zq.sword.array.transfer.message.MessageType;
import com.zq.sword.array.transfer.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 心跳请求处理器
 * @author: zhouqi1
 * @create: 2018-07-06 14:54
 **/
public class HeartBeatReqHandler extends TransferHandler {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            System.out.println("Client receive server heart beat message : --->" + message);
            ctx.fireChannelRead(msg);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    public class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            TransferMessage heatBeat = buildHeatBeat();
            System.out.println("Client send heart beat message to server : --> " + heatBeat);
            ctx.writeAndFlush(heatBeat);
    }

        private TransferMessage buildHeatBeat() {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
    }
}
