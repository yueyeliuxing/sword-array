package com.zq.sword.array.transfer.client;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeNamingInfo;
import com.zq.sword.array.netty.client.DefaultTransferClient;
import com.zq.sword.array.netty.client.TransferClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: sword 数据传输收集者
 * @author: zhouqi1
 * @create: 2018-10-24 15:42
 **/
public class SwordDataTransferGather implements DataTransferGather {

    /**
     * 维护的数据传输客户端
     */
    private Map<NodeId, TransferClient> transferClients;

    private SwordDataTransferGather(LeftOrderlyQueue<SwordData> leftOrderlyQueue,
                                     DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
        Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = dataConsumerServiceCoordinator.getNeedToConsumeNodeNamingInfo((DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent)->{
            if(transferClients == null){
                return;
            }
            Map<NodeId, NodeNamingInfo> nodeNamingInfoOfNodeId = dataEvent.getData();
            if(nodeNamingInfoOfNodeId == null || nodeNamingInfoOfNodeId.isEmpty()){
                return;
            }
            switch (dataEvent.getType()){
                //机器上线
                case NODE_MASTER_DATA_CHANGE:
                    for(NodeId nodeId : nodeNamingInfoOfNodeId.keySet()){
                        NodeNamingInfo nodeNamingInfo = nodeNamingInfoOfNodeId.get(nodeId);
                        TransferClient transferClient = transferClients.get(nodeId);
                        if(transferClient != null){
                            transferClient.disconnect();
                        }
                        TransferClient client = getTransferClient(nodeId, nodeNamingInfo, leftOrderlyQueue, dataConsumerServiceCoordinator);
                        client.connect();
                        transferClients.put(nodeId, client);
                    }
                    break;
                //机器掉线
                case NODE_MASTER_DATA_DELETE:
                    for(NodeId nodeId : nodeNamingInfoOfNodeId.keySet()){
                        TransferClient transferClient = transferClients.get(nodeId);
                        if(transferClient != null){
                            transferClient.disconnect();
                            transferClients.remove(nodeId);
                        }
                    }
                    break;
                default:
                    break;
            }
        });

        Map<NodeId, TransferClient> transferClients = new ConcurrentHashMap<>();
        if(nodeNamingInfosOfNodeId != null && !nodeNamingInfosOfNodeId.isEmpty()){
            nodeNamingInfosOfNodeId.forEach((clientNodeId, nodeNamingInfo)->{
                TransferClient transferClient = getTransferClient(clientNodeId, nodeNamingInfo, leftOrderlyQueue, dataConsumerServiceCoordinator);
                transferClients.put(clientNodeId, transferClient);
            });
        }
        this.transferClients = transferClients;
    }

    private TransferClient getTransferClient(NodeId clientNodeId, NodeNamingInfo nodeNamingInfo,
                                             LeftOrderlyQueue<SwordData> leftQueueService,
                                             DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
        TransferClient transferClient = new DefaultTransferClient(nodeNamingInfo.getHost(), nodeNamingInfo.getPort());
        transferClient.registerTransferHandler(new GatherSwordDataHandler(clientNodeId, leftQueueService, dataConsumerServiceCoordinator));
        return transferClient;
    }

    public static class SwordDataTransferGatherBuilder {

        private DataConsumerServiceCoordinator dataConsumerServiceCoordinator;

        private LeftOrderlyQueue<SwordData> leftOrderlyQueue;

        public static SwordDataTransferGatherBuilder create(){
            return new SwordDataTransferGatherBuilder();
        }

        public SwordDataTransferGatherBuilder bindingDataConsumerServiceCoordinator(DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
            this.dataConsumerServiceCoordinator = dataConsumerServiceCoordinator;
            return this;
        }
        public SwordDataTransferGatherBuilder bindingTargetDataSource(LeftOrderlyQueue<SwordData> leftOrderlyQueue){
            this.leftOrderlyQueue = leftOrderlyQueue;
            return this;
        }

        public SwordDataTransferGather build(){
            return new SwordDataTransferGather(leftOrderlyQueue, dataConsumerServiceCoordinator);
        }
    }

    @Override
    public void start() {
        if(transferClients == null){
            throw new NullPointerException("transferClients");
        }
        for(TransferClient transferClient : transferClients.values()){
            transferClient.connect();
        }
    }

    @Override
    public void stop() {
        if(transferClients == null){
            throw new NullPointerException("transferClients");
        }
        for(TransferClient transferClient : transferClients.values()){
            transferClient.disconnect();
        }
    }
}
