package com.zq.sword.array.metadata;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.metadata.data.ConsumedDataInfo;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeInfo;
import com.zq.sword.array.metadata.data.NodeNamingInfo;

import java.util.Map;

/**
 * @program: sword-array
 * @description: 主从服务协调者
 * @author: zhouqi1
 * @create: 2018-10-23 14:43
 **/
public interface DataConsumerServiceCoordinator {


    /**
     * 获取需要去消费节点的naming信息
     * @param nodeNamingInfoDataEventListener
     * @return
     */
    Map<NodeId, NodeNamingInfo> getNeedToConsumeNodeNamingInfo(DataEventListener<Map<NodeId, NodeNamingInfo>> nodeNamingInfoDataEventListener);

    /**
     * 获取消费的节点数据
     * @return
     */
    Map<NodeId, ConsumedDataInfo> getConsumedNodeDataInfo();


    /**
     * 提交已经消费节点的消费信息
     * @param consumeNodeId 要消费的节点
     * @param consumedDataInfo 消费信息
     * @return 提交是否成功
     */
    boolean commitConsumedDataInfo(NodeId consumeNodeId, ConsumedDataInfo consumedDataInfo);

}
