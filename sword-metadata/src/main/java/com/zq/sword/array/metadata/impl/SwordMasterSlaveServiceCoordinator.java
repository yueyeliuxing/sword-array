package com.zq.sword.array.metadata.impl;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.metadata.MasterSlaveServiceCoordinator;
import com.zq.sword.array.metadata.data.*;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 主从服务协调器
 * @author: zhouqi1
 * @create: 2018-10-23 16:17
 **/
public class SwordMasterSlaveServiceCoordinator implements MasterSlaveServiceCoordinator {

    private NodeId nodeId;

    private ZkClient zkClient;

    private static Map<NodeId, MasterSlaveServiceCoordinator> masterSlaveServiceCoordinators;

    static {
        masterSlaveServiceCoordinators = new ConcurrentHashMap<>();
    }

    public SwordMasterSlaveServiceCoordinator(NodeId nodeId, ZkClient zkClient) {
        this.nodeId = nodeId;
        this.zkClient = zkClient;
    }

    public synchronized static MasterSlaveServiceCoordinator buildMasterSlaveServiceCoordinator(NodeId nodeId, ZkClient zkClient){
        MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = masterSlaveServiceCoordinators.get(nodeId);
        if(masterSlaveServiceCoordinator ==  null){
            masterSlaveServiceCoordinator = new SwordMasterSlaveServiceCoordinator(nodeId, zkClient);
            masterSlaveServiceCoordinators.put(nodeId, masterSlaveServiceCoordinator);
        }
        return masterSlaveServiceCoordinator;
    }

    @Override
    public NodeInfo register(NodeNamingInfo nodeNamingInfo) {
        NodeInfo nodeInfo = new NodeInfo(nodeId);
        String masterRunningPath = ZkTreePathBuilder.buildNodeServerMasterRunningPath(nodeId);
        //master 节点已经存在
        if(zkClient.exists(masterRunningPath)){
            nodeInfo.setRole(NodeRole.PIPER_SLAVE);
        }else {
            nodeInfo.setRole(NodeRole.PIPER_MASTER);
            zkClient.createEphemeral(masterRunningPath, NodeNamingInfoBuilder.toNodeNamingInfoString(nodeNamingInfo));
        }
        return nodeInfo;
    }

    @Override
    public NodeNamingInfo getMasterNodeNamingInfo(DataEventListener<NodeNamingInfo> nodeNamingInfoDataEventListener) {
        String masterRunningPath = ZkTreePathBuilder.buildNodeServerMasterRunningPath(nodeId);
        zkClient.subscribeDataChanges(masterRunningPath, new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

                DataEvent<NodeNamingInfo> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_CONFIG_DATA_CHANGE);
                dataEvent.setData(NodeNamingInfoBuilder.buildNodeNamingInfo(data.toString()));
                nodeNamingInfoDataEventListener.listen(dataEvent);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                DataEvent<NodeNamingInfo> dataEvent = new DataEvent();
                dataEvent.setType(DataEventType.NODE_CONFIG_DATA_DELETE);
                nodeNamingInfoDataEventListener.listen(dataEvent);
            }
        });

        String nodeNamingInfoString = zkClient.readData(masterRunningPath);
        return NodeNamingInfoBuilder.buildNodeNamingInfo(nodeNamingInfoString);
    }
}
