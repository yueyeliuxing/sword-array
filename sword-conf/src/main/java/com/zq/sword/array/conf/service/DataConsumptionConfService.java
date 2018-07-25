package com.zq.sword.array.conf.service;

import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;

import java.util.List;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public interface DataConsumptionConfService extends Service {

    /**
     * 注册指定服务ID的数据消费信息
     * @param nodeConsumptionInfo 数据消费信息
     */
    void writeNodeConsumptionInfo(NodeConsumptionInfo nodeConsumptionInfo);

    /**
     * 获取指定服务ID的数据消费信息
     * @param nodeServerId 服务ID
     * @return 数据消费信息
     */
    List<NodeConsumptionInfo> listNodeConsumptionInfo(NodeServerId nodeServerId);

}
