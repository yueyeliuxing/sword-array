package com.zq.sword.array.transfer.client;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.data.ConsumedDataInfo;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.netty.handler.TransferHandler;
import com.zq.sword.array.netty.message.Header;
import com.zq.sword.array.netty.message.MessageType;
import com.zq.sword.array.netty.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class GatherSwordDataTransferHandler extends TransferHandler {

    private NodeId clientNodeServerId;

    private LeftOrderlyQueue<SwordData> leftQueueService;

    private DataConsumerServiceCoordinator dataConsumerServiceCoordinator;

    public GatherSwordDataTransferHandler(NodeId clientNodeServerId, LeftOrderlyQueue<SwordData> leftQueueService,
                                          DataConsumerServiceCoordinator dataConsumerServiceCoordinator) {
        this.clientNodeServerId = clientNodeServerId;
        this.leftQueueService = leftQueueService;
        this.dataConsumerServiceCoordinator = dataConsumerServiceCoordinator;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildPollTransferMessageReq(getLastDataId()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;

        if(message.getHeader() != null && message.getHeader().getType() == MessageType.PUSH_DATA_TRANSFER_RESP.value()) {
            Long lastDataId = getLastDataId();
            List<SwordData> dataItems = (List<SwordData>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(SwordData swordData : dataItems){
                    leftQueueService.push(swordData);
                    lastDataId = swordData.getId();
                }
                dataConsumerServiceCoordinator.commitConsumedDataInfo(clientNodeServerId, new ConsumedDataInfo(lastDataId));
            }
            ctx.fireChannelRead(buildPollTransferMessageReq(lastDataId));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPollTransferMessageReq(Long dataId) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_DATA_TRANSFER_REQ.value());
        message.setHeader(header);
        message.setBody(dataId);
        return message;
    }

    private Long getLastDataId(){
        Long lastDataId = 0L;
        Map<NodeId, ConsumedDataInfo> consumptionInfoMap = dataConsumerServiceCoordinator.getConsumedNodeDataInfo();
        if(consumptionInfoMap != null && !consumptionInfoMap.isEmpty()){
            ConsumedDataInfo consumedDataInfo = consumptionInfoMap.get(clientNodeServerId);
            lastDataId = consumedDataInfo.getDataId();
        }
        return lastDataId;
    }
}
