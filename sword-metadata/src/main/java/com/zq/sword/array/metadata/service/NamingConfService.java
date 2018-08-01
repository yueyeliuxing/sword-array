package com.zq.sword.array.metadata.service;

import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.common.event.DataEventListener;

import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public interface NamingConfService extends Service {

    /**
     * 注册指定服务ID的IP port 的数据信息
     * @param nodeServerId IP port 的数据信息
     * @param nodeServerInfo IP port 的数据信息
     */
    void registerNodeServerInfo(NodeServerId nodeServerId, NodeServerInfo nodeServerInfo);

    /**
     * 获取指定master的注册信息
     * @param nodeServerId
     * @return
     */
    NodeServerInfo getNodeServerInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> nodeMetadataInfoDataEventListener);

    /**
     * 获取其他PiperMaster服务的IP port 的数据信息
     * @return IP port 信息
     */
    Map<NodeServerId, NodeServerInfo> getConsumeDataMasterNodeServerInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> nodeMetadataInfoDataEventListener);
}
