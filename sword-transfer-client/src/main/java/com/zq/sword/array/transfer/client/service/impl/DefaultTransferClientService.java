package com.zq.sword.array.transfer.client.service.impl;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.metadata.service.NamingConfService;
import com.zq.sword.array.netty.client.DefaultTransferClient;
import com.zq.sword.array.netty.client.TransferClient;
import com.zq.sword.array.transfer.client.handler.PullTransferDataItemHandler;
import com.zq.sword.array.transfer.client.service.TransferClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 传输客户端的服务
 * @author: zhouqi1
 * @create: 2018-08-01 17:29
 **/
public class DefaultTransferClientService extends AbstractTaskService implements TransferClientService {

    private Logger logger = LoggerFactory.getLogger(DefaultTransferClientService.class);

    private Map<NodeServerId, TransferClient> transferClients;

    public NamingConfService getNamingConfService() {
        return ServiceContext.getInstance().findService(NamingConfService.class);
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        NodeServerId nodeServerId = serviceConfig.getId();
        NamingConfService namingConfService = getNamingConfService();
        if(namingConfService == null){
            logger.error("NamingConfService is not rigister");
            throw new NullPointerException("namingConfService is null");
        }
        Map<NodeServerId, NodeServerInfo> nodeServerInfoMap =  namingConfService.getConsumeDataMasterNodeServerInfo(nodeServerId, new DataEventListener<NodeMetadataInfo>() {
            @Override
            public void listen(DataEvent<NodeMetadataInfo> dataEvent) {
                NodeMetadataInfo nodeMetadataInfo = dataEvent.getData();
                NodeServerId nodeId = nodeMetadataInfo.getId();
                TransferClient transferClient = transferClients.get(nodeId);
                switch (dataEvent.getType()){
                    //机器上线
                    case NODE_MASTER_DATA_CHANGE:
                        if(transferClient != null){
                            NodeServerInfo nodeServerInfo = nodeMetadataInfo.getInfo();
                            TransferClient client = getTransferClient(nodeServerId, nodeId, nodeServerInfo);
                            client.reconnect();
                            transferClients.put(nodeId, client);
                        }
                        break;
                        //机器掉线
                    case NODE_MASTER_DATA_DELETE:
                        if(transferClient != null){
                            transferClient.disconnect();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        //初始化客户端
        initClients(nodeServerId, nodeServerInfoMap);

    }

    /**
     * 初始化客户端
     * @param nodeServerInfoMap
     */
    private void initClients(NodeServerId nodeServerId, Map<NodeServerId, NodeServerInfo> nodeServerInfoMap){
        transferClients = new ConcurrentHashMap<>();
        if(nodeServerInfoMap != null && !nodeServerInfoMap.isEmpty()){
            nodeServerInfoMap.forEach((clientNodeServerId, nodeServerInfo)->{
                TransferClient transferClient = getTransferClient(nodeServerId, clientNodeServerId, nodeServerInfo);
                transferClient.connect();
                transferClients.put(clientNodeServerId, transferClient);
            });
        }
    }

    private TransferClient getTransferClient(NodeServerId nodeServerId, NodeServerId clientNodeServerId, NodeServerInfo nodeServerInfo){
        TransferClient transferClient = new DefaultTransferClient(nodeServerInfo.getServerAddress(), nodeServerInfo.getPort());
        transferClient.registerTransferHandler(new PullTransferDataItemHandler(nodeServerId, clientNodeServerId));
        return transferClient;
    }

}
