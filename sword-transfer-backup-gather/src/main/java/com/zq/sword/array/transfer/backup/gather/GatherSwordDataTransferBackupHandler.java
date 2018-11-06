package com.zq.sword.array.transfer.backup.gather;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.netty.handler.TransferHandler;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.MessageType;
import com.zq.sword.array.netty.message.TransferMessage;
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
public class GatherSwordDataTransferBackupHandler extends TransferHandler {

    private volatile ScheduledFuture<?> gatherSwordDataFuture;

    private RightRandomQueue<SwordData> rightRandomQueue;
    private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

    public GatherSwordDataTransferBackupHandler(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue) {
        this.rightRandomQueue = rightRandomQueue;
        this.leftOrderlyQueue = leftOrderlyQueue;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (gatherSwordDataFuture != null) {
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
        TransferMessage message = (TransferMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_BACKUP_RESP.value()) {
            List<SwordData> dataItems = (List<SwordData>) message.getBody();
            if (dataItems != null && !dataItems.isEmpty()) {
                for (SwordData dataItem : dataItems) {
                    leftOrderlyQueue.push(dataItem);
                }
            }
            ctx.fireChannelRead(msg);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value()) {
            List<SwordData> dataItems = (List<SwordData>) message.getBody();
            if (dataItems != null && !dataItems.isEmpty()) {
                for (SwordData dataItem : dataItems) {
                    rightRandomQueue.push(dataItem);
                }
            }
            ctx.fireChannelRead(msg);
        } else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            if (gatherSwordDataFuture == null) {
                gatherSwordDataFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
            } else {
                ctx.fireChannelRead(msg);
            }
        } else {
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
            ctx.writeAndFlush(buildPollTLeftDataBackupReq());
            ctx.writeAndFlush(buildPollTRightDataBackupReq());
        }

        private TransferMessage buildPollTLeftDataBackupReq() {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.POLL_T_LEFT_DATA_BACKUP_REQ.value());
            message.setHeader(header);
            message.setBody(getLeftQueueLastDataId());
            return message;
        }

        private TransferMessage buildPollTRightDataBackupReq() {
            TransferMessage message = new TransferMessage();
            Header header = new Header();
            header.setType(MessageType.POLL_T_RIGHT_DATA_BACKUP_REQ.value());
            message.setHeader(header);
            message.setBody(getRightQueueLastDataId());
            return message;
        }

        private Long getRightQueueLastDataId() {
            return leftOrderlyQueue.getLastDataId();
        }

        private Long getLeftQueueLastDataId() {
            return leftOrderlyQueue.getLastDataId();
        }


    }
}
