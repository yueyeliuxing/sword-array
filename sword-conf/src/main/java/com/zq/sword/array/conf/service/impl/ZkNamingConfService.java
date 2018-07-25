package com.zq.sword.array.conf.service.impl;

import com.zq.sword.array.common.node.NodeServerType;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.common.node.NodeServerRole;
import com.zq.sword.array.conf.helper.NodeServerInfoHelper;
import com.zq.sword.array.conf.helper.ZkTreePathHelper;
import com.zq.sword.array.conf.listener.DataEvent;
import com.zq.sword.array.conf.listener.DataEventType;
import com.zq.sword.array.conf.listener.DataEventListener;
import com.zq.sword.array.conf.service.NamingConfService;
import com.zq.sword.array.conf.service.ZkService;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 命名服务配置
 * @author: zhouqi1
 * @create: 2018-07-23 16:20
 **/
public class ZkNamingConfService extends AbstractService implements NamingConfService {

    private ZkService zkService;

    public ZkNamingConfService(ZkService zkService) {
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
    public void registerMasterNodeServerChangeListener(NodeServerId nodeServerId, DataEventListener dataEventListener) {
        String nodeServerMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
        zkService.registerDataChangeListener(nodeServerMasterPath, new IZkDataListener(){


            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                DataEvent dataEvent = new DataEvent();
                dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                dataEvent.setData(data);
                dataEventListener.listen(dataEvent);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                DataEvent dataEvent = new DataEvent();
                dataEvent.setType(DataEventType.NODE_MASTER_DATA_DELETE);
                dataEventListener.listen(dataEvent);
            }
        });
    }

    @Override
    public void registerOtherMasterNodeServerInfoChangeListener(NodeServerId nodeServerId, DataEventListener dataEventListener) {
        String currentUnitName = nodeServerId.getUnitName();
        String unitNameData = zkService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (currentUnitName.equals(unitName)) {
                    continue;
                }
                nodeServerId.setUnitName(unitName);
                String unitMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
                String unitTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
                String type = zkService.readData(unitTypePath);
                NodeServerType nodeServerType = NodeServerType.valueOf(type);
                zkService.registerDataChangeListener(unitMasterPath, new IZkDataListener(){


                    @Override
                    public void handleDataChange(String dataPath, Object data) throws Exception {
                        NodeServerInfo nodeServerInfo =  NodeServerInfoHelper.getNodeServerInfo(data.toString());
                        nodeServerInfo.setId(ZkTreePathHelper.parseNodeServerMasterPath(dataPath));
                        nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);
                        nodeServerInfo.setType(nodeServerType);
                        DataEvent<NodeServerInfo> dataEvent = new DataEvent<>();
                        dataEvent.setType(DataEventType.NODE_OTHER_MASTER_DATA_CHANGE);
                        dataEvent.setData(nodeServerInfo);
                        dataEventListener.listen(dataEvent);
                    }

                    @Override
                    public void handleDataDeleted(String dataPath) throws Exception {
                        DataEvent dataEvent = new DataEvent();
                        dataEvent.setType(DataEventType.NODE_OTHER_MASTER_DATA_DELETE);
                        dataEventListener.listen(dataEvent);
                    }
                });

            }
        }
    }

    @Override
    public void registerNodeServerInfo(NodeServerInfo nodeServerInfo) {
        NodeServerId nodeServerId = nodeServerInfo.getId();
        String nodeServerMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
        String nodeServerTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
        String nodeServerSlavePath = ZkTreePathHelper.getNodeServerSlavePath(nodeServerId)+"/"+nodeServerInfo.getServerAddress();
        String nodeServerInfoData = NodeServerInfoHelper.getNodeServerInfoData(nodeServerInfo);

        //写入类型数据
        zkService.writeData(nodeServerTypePath, nodeServerInfo.getType().name());

        if (zkService.exists(nodeServerMasterPath)){
            zkService.createPersistent(nodeServerSlavePath);
            zkService.writeData(nodeServerSlavePath, nodeServerInfoData);
            nodeServerInfo.setRole(NodeServerRole.PIPER_SLAVE);
        }else {
            zkService.createEphemeral(nodeServerMasterPath, nodeServerInfoData);
            nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);

            //如果曾经是slave 删除曾经注册的slave节点
            if(zkService.exists(nodeServerSlavePath)){
                zkService.delete(nodeServerSlavePath);
            }
        }
    }

    @Override
    public NodeServerInfo getMasterNodeServerInfo(NodeServerId nodeServerId) {
        String nodeServerTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
        String type = zkService.readData(nodeServerTypePath);
        String nodeServerMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
        String nodeServerInfoData = zkService.readData(nodeServerMasterPath);
        NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(nodeServerInfoData);
        nodeServerInfo.setId(nodeServerId);
        nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);
        nodeServerInfo.setType( NodeServerType.valueOf(type));
        return nodeServerInfo;
    }

    @Override
    public List<NodeServerInfo> listOtherMasterNodeServerInfo(NodeServerId nodeServerId) {
        List<NodeServerInfo> nodeServerInfos = new ArrayList<>();
        String currentUnitName = nodeServerId.getUnitName();
        String unitNameData = zkService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (currentUnitName.equals(unitName)) {
                    continue;
                }
                nodeServerId.setUnitName(unitName);
                String nodeServerTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
                String type = zkService.readData(nodeServerTypePath);
                String unitMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
                String data = zkService.readData(unitMasterPath);
                NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(data);
                NodeServerId id = ZkTreePathHelper.parseNodeServerMasterPath(unitMasterPath);
                nodeServerInfo.setId(id);
                nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);
                nodeServerInfo.setType(NodeServerType.valueOf(type));
                nodeServerInfos.add(nodeServerInfo);
            }
        }
        return nodeServerInfos;
    }
}
