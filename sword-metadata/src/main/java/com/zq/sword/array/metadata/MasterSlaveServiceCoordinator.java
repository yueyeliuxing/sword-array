package com.zq.sword.array.metadata;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeInfo;
import com.zq.sword.array.metadata.data.NodeNamingInfo;

/**
 * @program: sword-array
 * @description: 主从服务协调者
 * @author: zhouqi1
 * @create: 2018-10-23 14:43
 **/
public interface MasterSlaveServiceCoordinator {

    /**
     * 服务注册
     * @param nodeNamingInfo
     * @return
     */
    NodeInfo register(NodeNamingInfo nodeNamingInfo);

    /***
     * 获取主节点的 naming 信息
     * @param nodeNamingInfoDataEventListener 主节点数据变化监听器
     * @return
     */
    NodeNamingInfo getMasterNodeNamingInfo(DataEventListener<NodeNamingInfo> nodeNamingInfoDataEventListener);




}
