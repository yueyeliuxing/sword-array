package com.zq.sword.array.metadata.impl;

import com.zq.sword.array.metadata.ConfigManager;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.MasterSlaveServiceCoordinator;
import com.zq.sword.array.metadata.MetadataCenter;
import com.zq.sword.array.metadata.data.*;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: zk 元数据管理中心
 * @author: zhouqi1
 * @create: 2018-10-22 21:09
 **/
public class ZkMatedataCenter implements MetadataCenter {

    private Logger logger = LoggerFactory.getLogger(ZkMatedataCenter.class);

    private ZkClient zkClient;

    public ZkMatedataCenter(String connectAddr, int sessionTimeOut) {
        logger.info("metadata module starting...");
        zkClient = new ZkClient(new ZkConnection(connectAddr), sessionTimeOut, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return (data.toString()).getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });
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
