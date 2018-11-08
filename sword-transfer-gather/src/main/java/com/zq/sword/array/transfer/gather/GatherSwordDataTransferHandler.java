package com.zq.sword.array.transfer.gather;

import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.data.ConsumedDataInfo;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.transfer.handler.TransferHandler;
import com.zq.sword.array.transfer.message.Header;
import com.zq.sword.array.transfer.message.MessageType;
import com.zq.sword.array.transfer.message.TransferMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据想传输处理器
 * @author: zhouqi1
 * @create: 2018-08-01 20:44
 **/
public class GatherSwordDataTransferHandler extends TransferHandler {

    private volatile ScheduledFuture<?> gatherSwordDataFuture;

    private NodeId clientNodeServerId;

    private DataQueue<SwordData> dataQueue;

    private DataConsumerServiceCoordinator dataConsumerServiceCoordinator;

    public GatherSwordDataTransferHandler(NodeId clientNodeServerId, DataQueue<SwordData> dataQueue,
                                          DataConsumerServiceCoordinator dataConsumerServiceCoordinator) {
        this.clientNodeServerId = clientNodeServerId;
        this.dataQueue = dataQueue;
        this.dataConsumerServiceCoordinator = dataConsumerServiceCoordinator;
    }

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

        if(message.getHeader() != null && message.getHeader().getType() == MessageType.PUSH_DATA_TRANSFER_RESP.value()) {
            Long lastDataId = 0L;
            List<SwordData> dataItems = (List<SwordData>)message.getBody();
            if(dataItems != null && !dataItems.isEmpty()){
                for(SwordData swordData : dataItems){
                    dataQueue.push(swordData);
                    lastDataId = swordData.getId();
                }
                dataConsumerServiceCoordinator.commitConsumedDataInfo(clientNodeServerId, new ConsumedDataInfo(lastDataId));
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


}
