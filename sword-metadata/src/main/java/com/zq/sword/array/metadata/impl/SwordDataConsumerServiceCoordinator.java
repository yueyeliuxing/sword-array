package com.zq.sword.array.metadata.impl;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.data.*;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zq.sword.array.metadata.data.ZkTreePathBuilder.*;

/**
 * @program: sword-array
 * @description: 主从服务协调器
 * @author: zhouqi1
 * @create: 2018-10-23 16:17
 **/
public class SwordDataConsumerServiceCoordinator implements DataConsumerServiceCoordinator {

    private NodeId nodeId;

    private ZkClient zkClient;

    private static Map<NodeId, DataConsumerServiceCoordinator> dataConsumerServiceCoordinators;

    static {
        dataConsumerServiceCoordinators = new ConcurrentHashMap<>();
    }

    public SwordDataConsumerServiceCoordinator(NodeId nodeId, ZkClient zkClient) {
        this.nodeId = nodeId;
        this.zkClient = zkClient;
    }

    public synchronized static DataConsumerServiceCoordinator buildDataConsumerServiceCoordinator(NodeId nodeId, ZkClient zkClient){
        DataConsumerServiceCoordinator dataConsumerServiceCoordinator = dataConsumerServiceCoordinators.get(nodeId);
        if(dataConsumerServiceCoordinator ==  null){
            dataConsumerServiceCoordinator = new SwordDataConsumerServiceCoordinator(nodeId, zkClient);
            dataConsumerServiceCoordinators.put(nodeId, dataConsumerServiceCoordinator);
        }
        return dataConsumerServiceCoordinator;
    }


    @Override
    public Map<NodeId, NodeNamingInfo> getNeedToConsumeNodeNamingInfo(DataEventListener<Map<NodeId, NodeNamingInfo>> nodeNamingInfoDataEventListener) {
        Map<NodeId, NodeNamingInfo> nodeNamingInfoOfNodeIds = new HashMap<>();
        List<String> dcPaths = zkClient.getChildren(ZkTreePathBuilder.ZK_ROOT);
        if(dcPaths != null && !dcPaths.isEmpty()){
            for(String dcPath : dcPaths){
                String dcName = dcPath;
                dcPath = ZkTreePathBuilder.ZK_ROOT+"/"+dcPath;
                List<String> unitCategoryPaths =  zkClient.getChildren(dcPath);
                if(unitCategoryPaths != null && !unitCategoryPaths.isEmpty()){
                    for(String unitCategoryPath : unitCategoryPaths){
                        unitCategoryPath = dcPath+"/"+unitCategoryPath;
                        boolean success = false;
                        NodeType nodeType = nodeId.getType();
                        if(nodeType.equals(NodeType.DC_UNIT_PIPER)
                                && dcName.equals(nodeId.getDc())
                                && (unitCategoryPath.endsWith(ZK_SWORD_UNITS) || unitCategoryPath.endsWith(ZK_SWORD_PROXY_UNITS))){
                            success = true;
                        }else if(nodeType.equals(NodeType.DC_UNIT_PROXY_PIPER)
                                && !dcName.equals(nodeId.getDc())
                                && unitCategoryPath.endsWith(ZK_SWORD_UNITS)){
                            success = true;
                        }

                        if(!success){
                            continue;
                        }
                        List<String> unitPaths =  zkClient.getChildren(unitCategoryPath);
                        if(unitPaths != null && !unitPaths.isEmpty()){
                            for(String unitPath : unitPaths){
                                unitPath = unitCategoryPath+"/"+unitPath;
                                List<String> unitSwordPaths =  zkClient.getChildren(unitPath);
                                if(unitSwordPaths != null && !unitSwordPaths.isEmpty()){
                                    for(String unitSwordPath : unitSwordPaths){
                                        unitSwordPath = unitPath+"/"+unitSwordPath;
                                        if(unitSwordPath.endsWith(ZK_SWORD_PIPER)){
                                            List<String> unitSwordPiperPaths = zkClient.getChildren(unitSwordPath);
                                            if(unitSwordPiperPaths != null && !unitSwordPiperPaths.isEmpty()){
                                                for(String unitSwordPiperPath : unitSwordPiperPaths){
                                                    unitSwordPiperPath = unitSwordPath + "/" + unitSwordPiperPath;
                                                    List<String> unitSwordPiperMetadataPaths = zkClient.getChildren(unitSwordPiperPath);
                                                    if(unitSwordPiperMetadataPaths != null && !unitSwordPiperMetadataPaths.isEmpty()){
                                                        NodeId consumedNodeId = ZkTreePathBuilder.parseNodeServerMasterPath(unitSwordPiperPath);
                                                        if(nodeId.equals(consumedNodeId)){
                                                            continue;
                                                        }
                                                        for(String unitSwordPiperMetadataPath : unitSwordPiperMetadataPaths){
                                                            unitSwordPiperMetadataPath = unitSwordPiperPath + "/" + unitSwordPiperMetadataPath;
                                                            if(unitSwordPiperMetadataPath.endsWith(ZK_SWORD_PIPER_MASTER)){
                                                                List<String> masterRunningPaths =  zkClient.getChildren(unitSwordPiperMetadataPath);
                                                                if(masterRunningPaths != null && !masterRunningPaths.isEmpty()){
                                                                    for(String masterRunningPath : masterRunningPaths){
                                                                        masterRunningPath = unitSwordPiperMetadataPath + "/" + masterRunningPath;
                                                                        if(masterRunningPath.endsWith(ZK_SWORD_PIPER_MASTER_RUNNING)){
                                                                            String masterInfo = zkClient.readData(masterRunningPath);
                                                                            NodeNamingInfo nodeNamingInfo = NodeNamingInfoBuilder.buildNodeNamingInfo(masterInfo);
                                                                            nodeNamingInfoOfNodeIds.put(consumedNodeId, nodeNamingInfo);

                                                                            zkClient.subscribeDataChanges(masterRunningPath, new IZkDataListener(){

                                                                                @Override
                                                                                public void handleDataChange(String dataPath, Object data) throws Exception {

                                                                                    DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent = new DataEvent<>();
                                                                                    dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                                                                                    Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = new HashMap<>();
                                                                                    nodeNamingInfosOfNodeId.put(consumedNodeId, NodeNamingInfoBuilder.buildNodeNamingInfo(data.toString()));
                                                                                    dataEvent.setData(nodeNamingInfosOfNodeId);
                                                                                    nodeNamingInfoDataEventListener.listen(dataEvent);
                                                                                }

                                                                                @Override
                                                                                public void handleDataDeleted(String dataPath) throws Exception {
                                                                                    DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent = new DataEvent();
                                                                                    dataEvent.setType(DataEventType.NODE_MASTER_DATA_DELETE);
                                                                                    Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = new HashMap<>();
                                                                                    nodeNamingInfosOfNodeId.put(consumedNodeId, nodeNamingInfo);
                                                                                    dataEvent.setData(nodeNamingInfosOfNodeId);
                                                                                    nodeNamingInfoDataEventListener.listen(dataEvent);
                                                                                }
                                                                            });

                                                                        }else if(masterRunningPath.endsWith(ZK_SWORD_PIPER_MASTER_STATER_STATE)){
                                                                            zkClient.subscribeDataChanges(masterRunningPath, new IZkDataListener(){

                                                                                @Override
                                                                                public void handleDataChange(String dataPath, Object data) throws Exception {

                                                                                    MasterStaterState masterStaterState = MasterStaterState.valueOf(data.toString());
                                                                                    if(masterStaterState.equals(MasterStaterState.STARTED)){
                                                                                        DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent = new DataEvent<>();
                                                                                        dataEvent.setType(DataEventType.NODE_MASTER_STATED);
                                                                                        Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = new HashMap<>();
                                                                                        nodeNamingInfosOfNodeId.put(consumedNodeId, new NodeNamingInfo());
                                                                                        dataEvent.setData(nodeNamingInfosOfNodeId);
                                                                                        nodeNamingInfoDataEventListener.listen(dataEvent);
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void handleDataDeleted(String dataPath) throws Exception {
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                                zkClient.subscribeChildChanges(unitSwordPiperMetadataPath, new IZkChildListener(){

                                                                    @Override
                                                                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                                                                        if(currentChilds != null && !currentChilds.isEmpty()){
                                                                            for(String currentChild : currentChilds){
                                                                                String masterRunningPath = parentPath + "/" + currentChild;
                                                                                if(masterRunningPath.endsWith(ZK_SWORD_PIPER_MASTER_RUNNING)) {
                                                                                    String masterInfo = zkClient.readData(masterRunningPath);
                                                                                    NodeNamingInfo nodeNamingInfo = NodeNamingInfoBuilder.buildNodeNamingInfo(masterInfo);

                                                                                    DataEvent<Map<NodeId, NodeNamingInfo>> dataEvent = new DataEvent();
                                                                                    dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                                                                                    Map<NodeId, NodeNamingInfo> nodeNamingInfosOfNodeId = new HashMap<>();
                                                                                    nodeNamingInfosOfNodeId.put(consumedNodeId, nodeNamingInfo);
                                                                                    dataEvent.setData(nodeNamingInfosOfNodeId);
                                                                                    nodeNamingInfoDataEventListener.listen(dataEvent);
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        nodeNamingInfoOfNodeIds.remove(nodeId);
        return Collections.unmodifiableMap(nodeNamingInfoOfNodeIds);
    }

    @Override
    public Map<NodeId, ConsumedDataInfo> getConsumedNodeDataInfo() {
        Map<NodeId, ConsumedDataInfo> consumedDataInfoOfNodeId = new HashMap<>();
        String consumedDataPath = ZkTreePathBuilder.buildConsumedNodeDataParentPath(nodeId);
        List<String> consumedUnitDataPaths = zkClient.getChildren(consumedDataPath);
        if(consumedUnitDataPaths != null && !consumedUnitDataPaths.isEmpty()){
            for(String consumedUnitDataPath : consumedUnitDataPaths){
                if(!consumedUnitDataPath.contains("|")){
                    continue;
                }
                NodeId id = NodeIdBuilder.buildNodeId(consumedUnitDataPath);
                String data =  zkClient.readData(consumedDataPath+"/"+consumedUnitDataPath);
                consumedDataInfoOfNodeId.put(id, new ConsumedDataInfo(Long.parseLong(data)));
            }
        }
        return Collections.unmodifiableMap(consumedDataInfoOfNodeId);
    }

    @Override
    public boolean commitConsumedDataInfo(NodeId consumeNodeId, ConsumedDataInfo consumedDataInfo) {
        String consumeUnitPath = ZkTreePathBuilder.buildConsumedNodeDataPath(nodeId, consumeNodeId);
        if(!zkClient.exists(consumeUnitPath)){
            zkClient.createPersistent(consumeUnitPath, true);
        }
        //zk修改
        zkClient.writeData(consumeUnitPath, consumedDataInfo.getDataId());
        return false;
    }
}
