package com.zq.sword.array.transfer.gather;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeNamingInfo;
import com.zq.sword.array.transfer.client.DefaultTransferClient;
import com.zq.sword.array.transfer.client.TransferClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: sword 数据传输收集者
 * @author: zhouqi1
 * @create: 2018-10-24 15:42
 **/
public class SwordDataTransferGather implements DataTransferGather {

    private Logger logger = LoggerFactory.getLogger(SwordDataTransferGather.class);

    /**
     * 维护的数据传输客户端
     */
    private Map<NodeId, TransferClient> transferClients;

    public SwordDataTransferGather(String host, int port, DataQueue<SwordData> dataQueue){
        this.transferClients = new ConcurrentHashMap<>();
        TransferClient transferClient = new DefaultTransferClient(host, port);
        transferClient.registerTransferHandler(new GatherSwordDataTransferHandler(null, dataQueue, null));
        transferClients.put(new NodeId(), transferClient);
    }

    private SwordDataTransferGather(DataQueue<SwordData> dataQueue,
                                     DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
        Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = dataConsumerServiceCoordinator.getNeedToConsumeNodeNamingInfo((DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent)->{
            if(transferClients == null){
                return;
            }
            Map<NodeId, NodeNamingInfo> nodeNamingInfoOfNodeId = dataEvent.getData();
            if(nodeNamingInfoOfNodeId == null || nodeNamingInfoOfNodeId.isEmpty()){
                return;
            }
            logger.info("消费piper改变{}", dataEvent);
            switch (dataEvent.getType()){
                //机器上线
                case NODE_MASTER_DATA_CHANGE:
                    for(NodeId nodeId : nodeNamingInfoOfNodeId.keySet()){
                        NodeNamingInfo nodeNamingInfo = nodeNamingInfoOfNodeId.get(nodeId);
                        TransferClient transferClient = transferClients.get(nodeId);
                        if(transferClient != null){
                            transferClient.disconnect();
                        }
                        TransferClient client = getTransferClient(nodeId, nodeNamingInfo, dataQueue, dataConsumerServiceCoordinator);
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
                //机器启动成功
                case NODE_MASTER_STATED:
                    for(NodeId nodeId : nodeNamingInfoOfNodeId.keySet()){
                        TransferClient transferClient = transferClients.get(nodeId);
                        if(transferClient != null){
                            transferClient.connect();
                        }
                    }
                    break;
                default:
                    break;
            }
        });
        logger.info("获取消费的piper地址{}", nodeNamingInfosOfNodeId);
        Map<NodeId, TransferClient> transferClients = new ConcurrentHashMap<>();
        if(nodeNamingInfosOfNodeId != null && !nodeNamingInfosOfNodeId.isEmpty()){
            nodeNamingInfosOfNodeId.forEach((clientNodeId, nodeNamingInfo)->{
                TransferClient transferClient = getTransferClient(clientNodeId, nodeNamingInfo, dataQueue, dataConsumerServiceCoordinator);
                transferClients.put(clientNodeId, transferClient);
            });
        }
        this.transferClients = transferClients;
    }

    private TransferClient getTransferClient(NodeId clientNodeId, NodeNamingInfo nodeNamingInfo,
                                             DataQueue<SwordData> dataQueue,
                                             DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
        TransferClient transferClient = new DefaultTransferClient(nodeNamingInfo.getHost(), nodeNamingInfo.getPort());
        transferClient.registerTransferHandler(new GatherSwordDataTransferHandler(clientNodeId, dataQueue, dataConsumerServiceCoordinator));
        return transferClient;
    }

    public static class SwordDataTransferGatherBuilder {

        private DataConsumerServiceCoordinator dataConsumerServiceCoordinator;

        private DataQueue<SwordData> dataQueue;

        public static SwordDataTransferGatherBuilder create(){
            return new SwordDataTransferGatherBuilder();
        }

        public SwordDataTransferGatherBuilder bindingDataConsumerServiceCoordinator(DataConsumerServiceCoordinator dataConsumerServiceCoordinator){
            this.dataConsumerServiceCoordinator = dataConsumerServiceCoordinator;
            return this;
        }

        public SwordDataTransferGatherBuilder bindingTargetDataSource(DataQueue<SwordData> dataQueue){
            this.dataQueue = dataQueue;
            return this;
        }

        public SwordDataTransferGather build(){
            return new SwordDataTransferGather(dataQueue, dataConsumerServiceCoordinator);
        }
    }

    @Override
    public void start() {
        new Thread(()->{
            if(transferClients == null){
                throw new NullPointerException("transferClients");
            }
            for(TransferClient transferClient : transferClients.values()){
                transferClient.connect();
            }
            logger.info("连接需要消费的piper启动成功");
        }).start();

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
