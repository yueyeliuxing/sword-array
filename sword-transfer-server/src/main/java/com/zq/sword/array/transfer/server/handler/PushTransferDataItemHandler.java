package com.zq.sword.array.transfer.server.handler;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.ServiceContext;
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
public class PushTransferDataItemHandler extends TransferHandler {

    private NodeServerId nodeServerId;

    private RightQueueService rightQueueService;

    public PushTransferDataItemHandler(NodeServerId nodeServerId) {
        this.nodeServerId = nodeServerId;
        rightQueueService = ServiceContext.getInstance().findService(RightQueueService.class);
        rightQueueService.registerDataItemListener(nodeServerId, new DataEventListener<DataItem>(){

            @Override
            public void listen(DataEvent<DataItem> dataEvent) {

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

        if(message.getHeader() != null && message.getHeader().getType() == MessageType.PUSH_T_RIGHT_DATA_TRANSFER_REQ.value()) {
            Long dataItemId = (Long)message.getBody();
            List<DataItem> dataItems = rightQueueService.pollAfterId(dataItemId);
            ctx.fireChannelRead(buildPushTransferMessageResp(dataItems));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPushTransferMessageResp(List<DataItem> dataItems) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.PUSH_T_RIGHT_DATA_TRANSFER_RESP.value());
        message.setHeader(header);
        message.setBody(dataItems);
        return message;
    }
}
