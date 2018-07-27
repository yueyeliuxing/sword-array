package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.metadata.helper.ZkTreePathHelper;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
import com.zq.sword.array.metadata.service.DataConfService;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: zk 消息消费配置信息
 * @author: zhouqi1
 * @create: 2018-07-24 10:13
 **/
public class ZkDataConsumptionConfService extends AbstractService implements DataConsumptionConfService {

    private DataConfService dataConfService;

    public ZkDataConsumptionConfService(DataConfService dataConfService) {
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
    public void writeNodeConsumptionInfo(NodeConsumptionInfo nodeConsumptionInfo) {
        String consumeOtherNodeServerDataPath = ZkTreePathHelper.getConsumeOtherNodeServerDataPath(nodeConsumptionInfo.getId(), nodeConsumptionInfo.getConsumeUnitName());
        dataConfService.writeData(consumeOtherNodeServerDataPath, nodeConsumptionInfo.getDataItemId());
    }

    @Override
    public List<NodeConsumptionInfo> listNodeConsumptionInfo(NodeServerId nodeServerId) {
        List<NodeConsumptionInfo> nodeConsumptionInfos = new ArrayList<>();
        String unitNameData = dataConfService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (nodeServerId.getUnitName().equals(unitName)) {
                    continue;
                }
                String consumeOtherNodeServerDataPath = ZkTreePathHelper.getConsumeOtherNodeServerDataPath(nodeServerId, unitName);
                String data = dataConfService.readData(consumeOtherNodeServerDataPath);
                NodeConsumptionInfo nodeConsumptionInfo = new NodeConsumptionInfo();
                nodeConsumptionInfo.setDataItemId(data);
                NodeServerId id = new NodeServerId();
                id.setDcName(nodeServerId.getDcName());
                id.setUnitName(unitName);
                id.setServerName(nodeServerId.getServerName());
                nodeConsumptionInfo.setId(id);
                nodeConsumptionInfos.add(nodeConsumptionInfo);
            }
        }
        return nodeConsumptionInfos;
    }
}
