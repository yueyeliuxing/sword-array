package com.zq.sword.array.conf.service.impl;

import com.zq.sword.array.common.node.NodeServerType;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.common.node.NodeServerRole;
import com.zq.sword.array.conf.helper.NodeServerInfoHelper;
import com.zq.sword.array.conf.helper.ZkTreePathHelper;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.conf.service.NamingConfService;
import com.zq.sword.array.conf.service.DataConfService;
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

    private DataConfService dataConfService;

    public ZkNamingConfService(DataConfService dataConfService) {
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
    public void registerMasterNodeServerChangeListener(NodeServerId nodeServerId, DataEventListener<NodeServerInfo> dataEventListener) {
        String nodeServerMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
        dataConfService.registerDataChangeListener(nodeServerMasterPath, new IZkDataListener(){


            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                DataEvent<NodeServerInfo> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                dataEvent.setData(NodeServerInfoHelper.getNodeServerInfo(data.toString()));
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
    public void registerOtherMasterNodeServerInfoChangeListener(NodeServerId nodeServerId, DataEventListener<NodeServerInfo> dataEventListener) {
        String currentUnitName = nodeServerId.getUnitName();
        String unitNameData = dataConfService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (currentUnitName.equals(unitName)) {
                    continue;
                }
                nodeServerId.setUnitName(unitName);
                String unitMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
                String unitTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
                String type = dataConfService.readData(unitTypePath);
                NodeServerType nodeServerType = NodeServerType.valueOf(type);
                dataConfService.registerDataChangeListener(unitMasterPath, new IZkDataListener(){


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
        dataConfService.writeData(nodeServerTypePath, nodeServerInfo.getType().name());

        if (dataConfService.exists(nodeServerMasterPath)){
            if(!dataConfService.exists(nodeServerSlavePath)){
                dataConfService.createPersistent(nodeServerSlavePath);
            }
            dataConfService.writeData(nodeServerSlavePath, nodeServerInfoData);
            nodeServerInfo.setRole(NodeServerRole.PIPER_SLAVE);
        }else {
            dataConfService.createEphemeral(nodeServerMasterPath, nodeServerInfoData);
            nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);

            //如果曾经是slave 删除曾经注册的slave节点
            if(dataConfService.exists(nodeServerSlavePath)){
                dataConfService.delete(nodeServerSlavePath);
            }
        }
    }

    @Override
    public NodeServerInfo getMasterNodeServerInfo(NodeServerId nodeServerId) {
        String nodeServerTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
        String type = dataConfService.readData(nodeServerTypePath);
        String nodeServerMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
        String nodeServerInfoData = dataConfService.readData(nodeServerMasterPath);
        NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(nodeServerInfoData);
        nodeServerInfo.setId(nodeServerId);
        nodeServerInfo.setRole(NodeServerRole.PIPER_MASTER);
        nodeServerInfo.setType(NodeServerType.valueOf(type));
        return nodeServerInfo;
    }

    @Override
    public List<NodeServerInfo> listOtherMasterNodeServerInfo(NodeServerId nodeServerId) {
        List<NodeServerInfo> nodeServerInfos = new ArrayList<>();
        String currentUnitName = nodeServerId.getUnitName();
        String unitNameData = dataConfService.readData(ZkTreePathHelper.getUnitNamePath(nodeServerId));
        String[] unitNames = unitNameData.split("|");
        if(unitNames != null && unitNames.length > 0) {
            for (String unitName : unitNames) {
                if (currentUnitName.equals(unitName)) {
                    continue;
                }
                nodeServerId.setUnitName(unitName);
                String nodeServerTypePath = ZkTreePathHelper.getNodeServerTypePath(nodeServerId);
                String type = dataConfService.readData(nodeServerTypePath);
                String unitMasterPath = ZkTreePathHelper.getNodeServerMasterPath(nodeServerId);
                String data = dataConfService.readData(unitMasterPath);
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
