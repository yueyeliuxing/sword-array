package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.metadata.service.NodeConfService;
import com.zq.sword.array.metadata.service.DataConfService;
import com.zq.sword.array.common.node.NodeMetadataInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class DefaultNodeConfService extends AbstractService implements NodeConfService {

    private Logger logger = LoggerFactory.getLogger(DefaultNodeConfService.class);

    private DataConfService dataConfService;

    public DefaultNodeConfService(DataConfService dataConfService) {
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
    public NodeServerConfig getNodeServerConfig(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> dataEventListener) {
        NodeMetadataInfo nodeMetadataInfo = dataConfService.getMetadataInfo(nodeServerId, dataEventListener);
        return nodeMetadataInfo.getConfig();
    }
}
