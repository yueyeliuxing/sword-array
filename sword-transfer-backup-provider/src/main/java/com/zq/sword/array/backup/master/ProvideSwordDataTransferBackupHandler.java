package com.zq.sword.array.backup.master;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.netty.handler.TransferHandler;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.MessageType;
import com.zq.sword.array.netty.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class ProvideSwordDataTransferBackupHandler extends TransferHandler {

    private RightRandomQueue<SwordData> rightRandomQueue;
    private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

    public ProvideSwordDataTransferBackupHandler(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue) {
        this.rightRandomQueue = rightRandomQueue;
        this.leftOrderlyQueue = leftOrderlyQueue;
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
        TransferMessage message = (TransferMessage)msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_RIGHT_DATA_BACKUP_REQ.value()) {
            Long dataItemId = (Long)message.getBody();
            List<SwordData> dataItems = rightRandomQueue.pollAfterId(dataItemId);
            ctx.fireChannelRead(buildPushTRightBackupDataResp(dataItems));
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_BACKUP_REQ.value()) {
            Long dataItemId = (Long)message.getBody();
            List<SwordData> dataItems = leftOrderlyQueue.pollAfterId(dataItemId);
            ctx.fireChannelRead(buildPushTLeftBackupDataResp(dataItems));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPushTLeftBackupDataResp(List<SwordData> dataItems) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_LEFT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataItems);
        return message;
    }

    private TransferMessage buildPushTRightBackupDataResp(List<SwordData> dataItems) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataItems);
        return message;
    }
}
