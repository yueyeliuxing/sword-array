package com.zq.sword.array.transfer.client.handler;

import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
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
public class PullTransferDataItemHandler extends TransferHandler {

    private NodeServerId nodeServerId;

    private NodeServerId clientNodeServerId;

    private LeftQueueService leftQueueService;

    private DataConsumptionConfService dataConsumptionConfService;

    public PullTransferDataItemHandler(NodeServerId nodeServerId, NodeServerId clientNodeServerId) {
        this.nodeServerId = nodeServerId;
        this.clientNodeServerId = clientNodeServerId;
        leftQueueService = ServiceContext.getInstance().findService(LeftQueueService.class);
        dataConsumptionConfService = ServiceContext.getInstance().findService(DataConsumptionConfService.class);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildPollTransferMessageReq(getDataItemId()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TransferMessage message = (TransferMessage)msg;

        if(message.getHeader() != null && message.getHeader().getType() == MessageType.PUSH_DATA_TRANSFER_RESP.value()) {
            Long lastDataItemId = getDataItemId();
            List<DataItem> dataItems = (List<DataItem>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(DataItem dataItem : dataItems){
                    leftQueueService.addDataItem(dataItem);
                    lastDataItemId = dataItem.getId();
                }
                dataConsumptionConfService.writeNodeConsumptionInfo(nodeServerId, new NodeConsumptionInfo(clientNodeServerId, lastDataItemId));

            }
            ctx.fireChannelRead(buildPollTransferMessageReq(lastDataItemId));
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private TransferMessage buildPollTransferMessageReq(Long dataItemId) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.POLL_DATA_TRANSFER_REQ.value());
        message.setHeader(header);
        message.setBody(dataItemId);
        return message;
    }

    private Long getDataItemId(){
        Long dataItemId = 0L;
        Map<NodeServerId, NodeConsumptionInfo> consumptionInfoMap = dataConsumptionConfService.getNodeConsumptionInfo(nodeServerId);
        if(consumptionInfoMap != null && !consumptionInfoMap.isEmpty()){
            NodeConsumptionInfo nodeConsumptionInfo = consumptionInfoMap.get(clientNodeServerId);
            dataItemId = nodeConsumptionInfo.getDataItemId();
        }
        return dataItemId;
    }
}
