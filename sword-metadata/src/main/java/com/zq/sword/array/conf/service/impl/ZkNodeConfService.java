package com.zq.sword.array.conf.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.conf.helper.ZkTreePathHelper;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.conf.service.NodeConfService;
import com.zq.sword.array.conf.service.DataConfService;
import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class ZkNodeConfService extends AbstractService implements NodeConfService {

    private Logger logger = LoggerFactory.getLogger(ZkNodeConfService.class);

    private DataConfService dataConfService;

    public ZkNodeConfService(DataConfService dataConfService) {
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
    public void registerNodeServerConfigListener(NodeServerId nodeServerId, DataEventListener<NodeServerConfig> dataEventListener) {
        dataConfService.registerDataChangeListener(ZkTreePathHelper.getNodeServerConfigPath(nodeServerId), new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                DataEvent<NodeServerConfig> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_CONFIG_DATA_CHANGE);
                dataEvent.setData(new NodeServerConfig(data.toString()));
                dataEventListener.listen(dataEvent);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                DataEvent dataEvent = new DataEvent();
                dataEvent.setType(DataEventType.NODE_CONFIG_DATA_DELETE);
                dataEventListener.listen(dataEvent);
            }
        });
    }

    @Override
    public NodeServerConfig getNodeServerConfig(NodeServerId nodeServerId) {
        String serverConfig = dataConfService.readData(ZkTreePathHelper.getNodeServerConfigPath(nodeServerId));
        return new NodeServerConfig(serverConfig);
    }
}
