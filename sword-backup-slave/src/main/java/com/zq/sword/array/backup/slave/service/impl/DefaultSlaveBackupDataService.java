package com.zq.sword.array.backup.slave.service.impl;

import com.zq.sword.array.backup.slave.handler.PullBackupDataHandler;
import com.zq.sword.array.backup.slave.service.SlaveBackupDataService;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.common.node.NodeServerRole;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.metadata.service.NamingConfService;
import com.zq.sword.array.netty.client.DefaultTransferClient;
import com.zq.sword.array.netty.client.TransferClient;

/**
 * @program: sword-array
 * @description: 备份数据服务
 * @author: zhouqi1
 * @create: 2018-08-04 13:47
 **/
public class DefaultSlaveBackupDataService extends AbstractTaskService implements SlaveBackupDataService {

    private TransferClient transferClient;

    private NodeServerId nodeServerId;

    private NodeServerId masterNodeServerId;

    @Override
    public void start(ServiceConfig serviceConfig) {
        nodeServerId = serviceConfig.getId();
        masterNodeServerId = new NodeServerId(nodeServerId.getDcName(),
                nodeServerId.getUnitCategoryName(), nodeServerId.getUnitName(), nodeServerId.getServerName());
        masterNodeServerId.setType(nodeServerId.getType());
        masterNodeServerId.setRole(NodeServerRole.PIPER_MASTER);
        NamingConfService namingConfService = getNamingConfService();
        NodeServerInfo masterNodeServerInfo = namingConfService.getNodeServerInfo(masterNodeServerId, new DataEventListener<NodeMetadataInfo>() {
            @Override
            public void listen(DataEvent<NodeMetadataInfo> dataEvent) {
                switch (dataEvent.getType()){
                    case NODE_MASTER_DATA_CHANGE:
                        transferClient.reconnect();
                        break;
                    case NODE_MASTER_DATA_DELETE:
                        transferClient.disconnect();
                        break;
                    default:
                        break;
                }
            }
        });

        startClient(masterNodeServerInfo);

    }

    private NamingConfService getNamingConfService(){
        return ServiceContext.getInstance().findService(NamingConfService.class);
    }


    private void startClient(NodeServerInfo masterNodeServerInfo){
        transferClient = getTransferClient(masterNodeServerInfo);
        transferClient.connect();
    }

    private TransferClient getTransferClient(NodeServerInfo masterNodeServerInfo){
        TransferClient transferClient = new DefaultTransferClient(masterNodeServerInfo.getServerAddress(), masterNodeServerInfo.getBackupPort());
        transferClient.registerTransferHandler(new PullBackupDataHandler());
        return transferClient;
    }
}
