package com.zq.sword.array.metadata.service;

import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.node.NodeServerConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.event.DataEventListener;

/**
 * @program: sword-array
 * @description: NodeServer配置处理
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public interface NodeConfService extends Service {

    /**
     * 获取指定服务ID的服务配置信息
     * @param nodeServerId 服务ID
     * @return 配置信息
     */
    NodeServerConfig getNodeServerConfig(NodeServerId nodeServerId);

    /**
     * 获取指定服务ID的服务配置信息
     * @param nodeServerId 服务ID
     * @return 配置信息
     */
    NodeServerConfig getNodeServerConfig(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> dataEventListener);
}
