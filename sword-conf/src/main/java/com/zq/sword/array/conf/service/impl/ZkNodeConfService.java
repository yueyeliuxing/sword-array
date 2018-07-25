package com.zq.sword.array.conf.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.conf.helper.ZkTreePathHelper;
import com.zq.sword.array.conf.listener.DataEvent;
import com.zq.sword.array.conf.listener.DataEventType;
import com.zq.sword.array.conf.listener.DataEventListener;
import com.zq.sword.array.conf.service.NodeConfService;
import com.zq.sword.array.conf.service.ZkService;
import org.I0Itec.zkclient.IZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class ZkNodeConfService extends AbstractService implements NodeConfService {

    private Logger logger = LoggerFactory.getLogger(ZkNodeConfService.class);

    private ZkService zkService;

    public ZkNodeConfService(ZkService zkService) {
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
    public void registerNodeServerConfigChangeListenter(NodeServerId nodeServerId, DataEventListener dataEventListener) {
        zkService.registerDataChangeListener(ZkTreePathHelper.getNodeServerConfigPath(nodeServerId), new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                DataEvent dataEvent = new DataEvent();
                dataEvent.setType(DataEventType.NODE_CONFIG_DATA_CHANGE);
                dataEvent.setData(data);
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
        Properties properties = null;
        String serverConfig = zkService.readData(ZkTreePathHelper.getNodeServerConfigPath(nodeServerId));
        try{
            properties = new Properties();
            properties.load(new ByteArrayInputStream(serverConfig.getBytes(Charset.defaultCharset())));
            return new NodeServerConfig(properties);
        }catch (Exception e){
            logger.error("加载服务配置文件转换为Properties出错", e);
            return null;
        }
    }
}
