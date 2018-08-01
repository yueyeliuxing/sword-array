package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.node.*;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.metadata.helper.NodeServerInfoHelper;
import com.zq.sword.array.metadata.helper.ZkTreePathHelper;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.metadata.service.NamingConfService;
import com.zq.sword.array.metadata.service.DataConfService;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 命名服务配置
 * @author: zhouqi1
 * @create: 2018-07-23 16:20
 **/
public class DefaultNamingConfService extends AbstractService implements NamingConfService {

    private DataConfService dataConfService;

    public DefaultNamingConfService(DataConfService dataConfService) {
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
    public void registerNodeServerInfo(NodeServerId nodeServerId, NodeServerInfo nodeServerInfo) {
        boolean success = dataConfService.setNodeServerMetadataMasterInfo(nodeServerId, nodeServerInfo);
        nodeServerId.setRole(NodeServerRole.PIPER_MASTER);
        if(!success){
            nodeServerId.setRole(NodeServerRole.PIPER_SLAVE);
        }
    }

    @Override
    public NodeServerInfo getNodeServerInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> nodeMetadataInfoDataEventListener) {
        NodeMetadataInfo nodeMetadataInfo = dataConfService.getMetadataInfo(nodeServerId, nodeMetadataInfoDataEventListener);
        return nodeMetadataInfo.getInfo();
    }

    @Override
    public Map<NodeServerId, NodeServerInfo> getConsumeDataMasterNodeServerInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> nodeMetadataInfoDataEventListener) {
        Map<NodeServerId, NodeServerInfo> nodeServerInfos = new HashMap<>();
        Map<NodeServerId, NodeMetadataInfo> unitsNodeMetadataInfo =  dataConfService.getAllUnitMetadataInfo(nodeServerId, nodeMetadataInfoDataEventListener);
        Map<NodeServerId, NodeMetadataInfo> proxyUnitsNodeMetadataInfo =  dataConfService.getAllProxyUnitMetadataInfo(nodeServerId, nodeMetadataInfoDataEventListener);
        Map<NodeServerId, NodeMetadataInfo> otherProxyUnitsNodeMetadataInfo =  dataConfService.getAllOtherProxyUnitMetadataInfo(nodeServerId, nodeMetadataInfoDataEventListener);

        Map<NodeServerId, NodeMetadataInfo> nodeMetadataInfoMap = new HashMap<>();
        switch (nodeServerId.getType()){
            case DC_UNIT_PIPER:
                nodeMetadataInfoMap.putAll(unitsNodeMetadataInfo);
                nodeMetadataInfoMap.putAll(proxyUnitsNodeMetadataInfo);
                break;
            case DC_UNIT_PROXY_PIPER:
                break;
            case OTHER_DC_UNIT_PROXY_PIPER:
                nodeMetadataInfoMap.putAll(unitsNodeMetadataInfo);
                break;
                default:
                    break;
        }


        nodeMetadataInfoMap.forEach((k,v)->{
            nodeServerInfos.put(k, v.getInfo());
        });

        return nodeServerInfos;
    }
}
