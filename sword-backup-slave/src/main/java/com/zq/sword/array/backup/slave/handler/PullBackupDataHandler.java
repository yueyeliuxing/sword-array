package com.zq.sword.array.backup.slave.handler;

import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.data.rqueue.service.RightQueueService;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
import com.zq.sword.array.netty.handler.TransferHandler;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.MessageType;
import com.zq.sword.array.netty.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class PullBackupDataHandler extends TransferHandler {

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
        ctx.writeAndFlush(buildPollTLeftDataBackupReq(getLastLeftQueueDataItemId()));
        ctx.writeAndFlush(buildPollTRightDataBackupReq(getLastRightQueueDataItemId()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;
        LeftQueueService leftQueueService = getLeftQueueService();
        RightQueueService rightQueueService = getRightQueueService();
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_TRANSFER_RESP.value()) {
            Long lastDataItemId = getLastLeftQueueDataItemId();
            List<DataItem> dataItems = (List<DataItem>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(DataItem dataItem : dataItems){
                    leftQueueService.addDataItem(dataItem);
                    lastDataItemId = dataItem.getId();
                }
            }
            ctx.fireChannelRead(buildPollTLeftDataBackupReq(lastDataItemId));
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value()){
            Long lastDataItemId = getLastRightQueueDataItemId();
            List<com.zq.sword.array.data.rqueue.domain.DataItem> dataItems = (List<com.zq.sword.array.data.rqueue.domain.DataItem>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(com.zq.sword.array.data.rqueue.domain.DataItem dataItem : dataItems){
                    rightQueueService.push(dataItem);
                    lastDataItemId = dataItem.getId();
                }
            }
            ctx.fireChannelRead(buildPollTLeftDataBackupReq(lastDataItemId));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPollTLeftDataBackupReq(Long dataItemId) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_LEFT_DATA_BACKUP_REQ.value());
        message.setHeader(header);
        message.setBody(dataItemId);
        return message;
    }

    private TransferMessage buildPollTRightDataBackupReq(Long dataItemId) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_RIGHT_DATA_BACKUP_REQ.value());
        message.setHeader(header);
        message.setBody(dataItemId);
        return message;
    }

    private Long getLastRightQueueDataItemId(){
        return getRightQueueService().getLastDataItemId();
    }

    private Long getLastLeftQueueDataItemId(){
       return getLeftQueueService().getLastDataItemId();
    }
}
