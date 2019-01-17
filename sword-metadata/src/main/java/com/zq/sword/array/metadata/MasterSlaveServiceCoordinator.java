package com.zq.sword.array.metadata;

import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.metadata.data.MasterStaterState;
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
     * 设置master 状态
     */
    void setMasterStaterState(MasterStaterState masterStaterState);

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
    NodeNamingInfo getMasterNodeNamingInfo(HotspotEventListener<NodeNamingInfo> nodeNamingInfoDataEventListener);




}
