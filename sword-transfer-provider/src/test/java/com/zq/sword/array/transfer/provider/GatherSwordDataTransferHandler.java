package com.zq.sword.array.transfer.provider;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.netty.handler.TransferHandler;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.MessageType;
import com.zq.sword.array.netty.message.TransferMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
@ChannelHandler.Sharable
public class GatherSwordDataTransferHandler extends TransferHandler {

    private volatile ScheduledFuture<?> gatherSwordDataFuture;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(gatherSwordDataFuture != null) {
            gatherSwordDataFuture.cancel(true);
            gatherSwordDataFuture = null;
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
        System.out.println("TransferMessage---"+message);
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.PUSH_DATA_TRANSFER_RESP.value()) {
            List<SwordData> dataItems = (List<SwordData>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(SwordData swordData : dataItems){
                    System.out.println(swordData + "-----------");
                }
            }
            ctx.fireChannelRead(msg);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            if(gatherSwordDataFuture == null) {
                gatherSwordDataFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            }else {
                ctx.fireChannelRead(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }



    private Long getLastDataId(){
        Long lastDataId = 0L;
        return lastDataId;
    }

    public class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            TransferMessage heatBeat = buildPollTransferMessageReq();
            System.out.println("Client send heart beat message to server : --> " + heatBeat);
            ctx.writeAndFlush(heatBeat);
        }

        private TransferMessage buildPollTransferMessageReq() {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.POLL_DATA_TRANSFER_REQ.value());
            message.setHeader(header);
            message.setBody(getLastDataId());
            return message;
        }
    }
}
