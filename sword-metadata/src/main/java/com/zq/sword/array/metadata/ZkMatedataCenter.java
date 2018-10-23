package com.zq.sword.array.metadata;

import com.zq.sword.array.metadata.data.*;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

/**
 * @program: sword-array
 * @description: zk 元数据管理中心
 * @author: zhouqi1
 * @create: 2018-10-22 21:09
 **/
public class ZkMatedataCenter implements MetadataCenter {

    private ZkClient zkClient;

    public ZkMatedataCenter(String connectAddr, int sessionTimeOut) {
        zkClient = new ZkClient(new ZkConnection(connectAddr), sessionTimeOut);
    }

    @Override
    public MasterSlaveServiceCoordinator getMasterSlaveServiceCoordinator(NodeId nodeId) {
        return SwordMasterSlaveServiceCoordinator.buildMasterSlaveServiceCoordinator(nodeId, zkClient);
    }

    @Override
    public DataConsumerServiceCoordinator getDataConsumerServiceCoordinator(NodeId nodeId) {
        return SwordDataConsumerServiceCoordinator.buildDataConsumerServiceCoordinator(nodeId, zkClient);
    }

    @Override
    public ConfigManager getConfigManager(NodeId nodeId) {
        return  SwordConfigManager.buildConfigManager(nodeId, zkClient);
    }
}
