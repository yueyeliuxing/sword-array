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

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class GatherSwordDataTransferBackupHandler extends TransferHandler {

    private RightRandomQueue<SwordData> rightRandomQueue;
    private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

    public GatherSwordDataTransferBackupHandler(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue) {
        this.rightRandomQueue = rightRandomQueue;
        this.leftOrderlyQueue = leftOrderlyQueue;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildPollTLeftDataBackupReq(getLastDataId()));
        ctx.writeAndFlush(buildPollTRightDataBackupReq(getLastDataId()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_BACKUP_RESP.value()) {
            Long lastSwordDataId = getLastDataId();
            List<SwordData> dataItems = (List<SwordData>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(SwordData dataItem : dataItems){
                    leftOrderlyQueue.push(dataItem);
                    lastSwordDataId = dataItem.getId();
                }
            }
            ctx.fireChannelRead(buildPollTLeftDataBackupReq(lastSwordDataId));
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value()){
            Long lastSwordDataId = getLastDataId();
            List<SwordData> dataItems = (List<SwordData>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(SwordData dataItem : dataItems){
                    rightRandomQueue.push(dataItem);
                    lastSwordDataId = dataItem.getId();
                }
            }
            ctx.fireChannelRead(buildPollTLeftDataBackupReq(lastSwordDataId));
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

    private Long getLastDataId(){
       return leftOrderlyQueue.getLastDataId();
    }
}
