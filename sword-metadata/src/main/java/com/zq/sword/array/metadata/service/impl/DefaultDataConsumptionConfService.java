package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.metadata.helper.ZkTreePathHelper;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
import com.zq.sword.array.metadata.service.DataConfService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: zk 消息消费配置信息
 * @author: zhouqi1
 * @create: 2018-07-24 10:13
 **/
public class DefaultDataConsumptionConfService extends AbstractService implements DataConsumptionConfService {

    private DataConfService dataConfService;

    public DefaultDataConsumptionConfService(DataConfService dataConfService) {
        this.dataConfService = dataConfService;
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        if(!dataConfService.isStart()){
            dataConfService.start();
        }
        start();
    }

    @Override
    public void writeNodeConsumptionInfo(NodeServerId nodeServerId, NodeConsumptionInfo nodeConsumptionInfo) {
        dataConfService.setNodeServerMetadataConsumeUnitData(nodeServerId, nodeConsumptionInfo);
    }

    @Override
    public Map<NodeServerId, NodeConsumptionInfo> getNodeConsumptionInfo(NodeServerId nodeServerId) {
        NodeMetadataInfo nodeMetadataInfo = dataConfService.getMetadataInfo(nodeServerId);
        if(nodeMetadataInfo != null){
            return nodeMetadataInfo.getConsumeData();
        }
        return  null;
    }
}
