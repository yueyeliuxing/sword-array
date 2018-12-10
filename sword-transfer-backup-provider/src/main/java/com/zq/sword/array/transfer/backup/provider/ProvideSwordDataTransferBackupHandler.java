package com.zq.sword.array.transfer.backup.provider;

import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.transfer.handler.TransferHandler;
import com.zq.sword.array.transfer.message.Header;
import com.zq.sword.array.transfer.message.MessageType;
import com.zq.sword.array.transfer.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class ProvideSwordDataTransferBackupHandler extends TransferHandler {

    private DataModify dataModify;

    private RightRandomQueue<SwordData> rightRandomQueue;
    private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

    public ProvideSwordDataTransferBackupHandler(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue) {
        this.rightRandomQueue = rightRandomQueue;
        this.leftOrderlyQueue = leftOrderlyQueue;
    }

    private void initDataModify(RightRandomQueue<SwordData> rightRandomQueue, LeftOrderlyQueue<SwordData> leftOrderlyQueue){
        this.dataModify = new DataModify();
        rightRandomQueue.registerSwordDataListener((event)->{
            if(event.getType().equals(DataEventType.SWORD_DATA_ADD)){
                dataModify.addRightAddData(event.getData());
            }
        });
        leftOrderlyQueue.registerSwordDataListener((event)->{
            if(event.getType().equals(DataEventType.SWORD_DATA_ADD)){
                dataModify.addLeftAddData(event.getData());
            }else if(event.getType().equals(DataEventType.SWORD_DATA_DEL)){
                dataModify.addLeftDelData(event.getData());
            }
        });
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
            ctx.fireChannelRead(buildPushTRightBackupDataResp());
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_BACKUP_REQ.value()) {
            ctx.fireChannelRead(buildPushTLeftBackupDataResp());
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.POLL_T_LEFT_DATA_DEL_BACKUP_REQ.value()) {
            ctx.fireChannelRead(buildPushTLeftBackupDelDataResp());
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPushTLeftBackupDelDataResp() {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_LEFT_DATA_DEL_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataModify.removeAllLeftDelDatas());
        return message;
    }

    private TransferMessage buildPushTLeftBackupDataResp() {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_LEFT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataModify.removeAllLeftAddDatas());
        return message;
    }

    private TransferMessage buildPushTRightBackupDataResp() {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_T_RIGHT_DATA_BACKUP_RESP.value());
        message.setHeader(header);
        message.setBody(dataModify.removeAllRightAddDatas());
        return message;
    }
}
