package com.zq.sword.array.conf.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeConsumptionInfo;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.conf.helper.ZkTreePathHelper;
import com.zq.sword.array.conf.service.DataConsumptionConfService;
import com.zq.sword.array.conf.service.ZkService;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: zk 消息消费配置信息
 * @author: zhouqi1
 * @create: 2018-07-24 10:13
 **/
public class ZkDataConsumptionConfService extends AbstractService implements DataConsumptionConfService {

    private ZkService zkService;

    public ZkDataConsumptionConfService(ZkService zkService) {
        this.zkService = zkService;
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        if(!zkService.isStart()){
            zkService.start();
        }
        start();
    }

    @Override
    public void writeNodeConsumptionInfo(NodeConsumptionInfo nodeConsumptionInfo) {
        String consumeOtherNodeServerDataPath = ZkTreePathHelper.getConsumeOtherNodeServerDataPath(nodeConsumptionInfo.getId(), nodeConsumptionInfo.getConsumeUnitName());
        zkService.writeData(consumeOtherNodeServerDataPath, nodeConsumptionInfo.getDataItemId());
    }

    @Override
    public List<NodeConsumptionInfo> listNodeConsumptionInfo(NodeServerId nodeServerId) {
        List<NodeConsumptionInfo> nodeConsumptionInfos = new ArrayList<>();
        String unitNameData = zkService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (nodeServerId.getUnitName().equals(unitName)) {
                    continue;
                }
                String consumeOtherNodeServerDataPath = ZkTreePathHelper.getConsumeOtherNodeServerDataPath(nodeServerId, unitName);
                String data = zkService.readData(consumeOtherNodeServerDataPath);
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
