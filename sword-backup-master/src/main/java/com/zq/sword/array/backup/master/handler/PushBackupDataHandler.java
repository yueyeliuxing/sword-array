package com.zq.sword.array.backup.master.handler;

import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.data.rqueue.domain.DataItem;
import com.zq.sword.array.data.rqueue.service.RightQueueService;
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
public class PushBackupDataHandler extends TransferHandler {

    private LeftQueueService getLeftQueueService(){
        return ServiceContext.getInstance().findService(LeftQueueService.class);
    }

    private RightQueueService getRightQueueService(){
        return ServiceContext.getInstance().findService(RightQueueService.class);
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
        RightQueueService rightQueueService = getRightQueueService();
        LeftQueueService leftQueueService = getLeftQueueService();
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_RIGHT_DATA_BACKUP_REQ.value()) {
            Long dataItemId = (Long)message.getBody();
            List<DataItem> dataItems = rightQueueService.pollAfterId(dataItemId);
            ctx.fireChannelRead(buildPushTRightBackupDataResp(dataItems));
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_BACKUP_REQ.value()) {
            Long dataItemId = (Long)message.getBody();
            List<com.zq.sword.array.data.lqueue.domain.DataItem> dataItems = leftQueueService.pollAfterId(dataItemId);
            ctx.fireChannelRead(buildPushTLeftBackupDataResp(dataItems));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPushTLeftBackupDataResp(List<com.zq.sword.array.data.lqueue.domain.DataItem> dataItems) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_LEFT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataItems);
        return message;
    }

    private TransferMessage buildPushTRightBackupDataResp(List<DataItem> dataItems) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataItems);
        return message;
    }
}
