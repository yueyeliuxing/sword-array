package com.zq.sword.array.metadata;

import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeInfo;
import com.zq.sword.array.metadata.data.NodeNamingInfo;

/**
 * @program: sword-array
 * @description: 元数据中心
 * @author: zhouqi1
 * @create: 2018-10-22 20:39
 **/
public interface MetadataCenter {

    /**
     * 返回主从服务协调器
     * @param nodeId
     * @return
     */
    MasterSlaveServiceCoordinator getMasterSlaveServiceCoordinator(NodeId nodeId);

    /**
     * 返回 消费节点数据协调器
     * @param nodeId
     * @return
     */
    DataConsumerServiceCoordinator getDataConsumerServiceCoordinator(NodeId nodeId);


    /**
     * 返回配置管理器
     * @return
     */
    ConfigManager getConfigManager(NodeId nodeId);
}
