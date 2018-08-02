package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.node.*;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.metadata.helper.NodeServerInfoHelper;
import com.zq.sword.array.metadata.helper.ZkTreePathHelper;
import com.zq.sword.array.metadata.service.DataConfService;
import com.zq.sword.array.common.node.NodeMetadataInfo;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.zq.sword.array.metadata.helper.ZkTreePathHelper.*;


/**
 * @program: sword-array
 * @description: Zk服务
 * @author: zhouqi1
 * @create: 2018-07-24 10:26
 **/
public class ZkDataConfService extends AbstractService implements DataConfService {

    /**
     * 单元 元数据字典
     */
    private Map<NodeServerId, NodeMetadataInfo> unitMetadataDic;

    /**
     * 本机房代理单元 元数据字典
     */
    private Map<NodeServerId, NodeMetadataInfo> proxyUnitMetadataDic;

    /**
     * 其他机房代理单元 元数据字典
     */
    private Map<NodeServerId, NodeMetadataInfo> otherProxyUnitMetadataDic;

    /**
     * 单元元数据监听器
     */
    private List<DataEventListener<NodeMetadataInfo>> unitsMetadataInfoListeners;

    /**
     * 代理单元元数据监听器
     */
    private List<DataEventListener<NodeMetadataInfo>> proxyUnitsMetadataInfoListeners;

    /**
     * 其他机房代理元数据监听器
     */
    private List<DataEventListener<NodeMetadataInfo>> otherDcProxyMetadataInfoListeners;


    /**
     * 其他机房代理元数据监听器
     */
    private volatile DataEventListener<NodeMetadataInfo> ownNodeServerConfigListener;

    private ZkClient zkClient;

    public ZkDataConfService() {
        unitMetadataDic = new ConcurrentHashMap<>();
        proxyUnitMetadataDic = new ConcurrentHashMap<>();
        otherProxyUnitMetadataDic = new ConcurrentHashMap<>();
        this.unitsMetadataInfoListeners = new CopyOnWriteArrayList<>();
        this.proxyUnitsMetadataInfoListeners = new CopyOnWriteArrayList<>();
        this.otherDcProxyMetadataInfoListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        super.start();
        String connectAddr = serviceConfig.getProperty(NodeServerConfigKey.ZK_CONNECT_ADDR);
        int sessionTimeOut = serviceConfig.getProperty(NodeServerConfigKey.ZK_CONNECT_TIMEOUT, Integer.class);
        zkClient = new ZkClient(new ZkConnection(connectAddr), sessionTimeOut);
        
        initMetadataDic();

        registerDataListeners();
    }

    private void initMetadataDic() {
        List<String> dcPaths = zkClient.getChildren(ZkTreePathHelper.ZK_ROOT);
        if(dcPaths != null && !dcPaths.isEmpty()){
            for(String dcPath : dcPaths){
                String dcName = dcPath.substring(dcPath.lastIndexOf("/")+1);
                List<String> unitCategoryPaths =  zkClient.getChildren(dcPath);
                if(unitCategoryPaths != null && !unitCategoryPaths.isEmpty()){
                    for(String unitCategoryPath : unitCategoryPaths){
                        NodeServerType nodeServerType = null;
                        String unitCategoryName = unitCategoryPath.substring(unitCategoryPath.lastIndexOf("/")+1);
                        Map<NodeServerId, NodeMetadataInfo> metadataInfoMap = null;
                        if(unitCategoryPath.endsWith(ZK_SWORD_UNITS)){
                            metadataInfoMap = unitMetadataDic;
                            nodeServerType = NodeServerType.DC_UNIT_PIPER;
                        }else if(unitCategoryPath.endsWith(ZK_SWORD_PROXY_UNITS)){
                            metadataInfoMap = proxyUnitMetadataDic;
                            nodeServerType = NodeServerType.DC_UNIT_PROXY_PIPER;
                        }else if(unitCategoryPath.endsWith(ZK_SWORD_OTHER_PROXY_UNITS)){
                            metadataInfoMap = otherProxyUnitMetadataDic;
                            nodeServerType = NodeServerType.OTHER_DC_UNIT_PROXY_PIPER;
                        }

                        if(metadataInfoMap == null){
                            continue;
                        }
                        List<String> unitPaths =  zkClient.getChildren(unitCategoryPath);
                        if(unitPaths != null && !unitPaths.isEmpty()){
                            for(String unitPath : unitPaths){
                                String unitName = unitPath.substring(unitPath.lastIndexOf("/")+1);
                                List<String> unitSwordPaths =  zkClient.getChildren(unitPath);
                                if(unitSwordPaths != null && !unitSwordPaths.isEmpty()){
                                    for(String unitSwordPath : unitSwordPaths){
                                        if(unitSwordPath.endsWith(ZK_SWORD_PIPER)){
                                            List<String> unitSwordPiperPaths = zkClient.getChildren(unitSwordPath);
                                            if(unitSwordPiperPaths != null && !unitSwordPiperPaths.isEmpty()){
                                                for(String unitSwordPiperPath : unitSwordPiperPaths){
                                                    String serverName = unitSwordPiperPath.substring(unitSwordPiperPath.lastIndexOf("/")+1);
                                                    List<String> unitSwordPiperMetadataPaths = zkClient.getChildren(unitSwordPiperPath);
                                                    if(unitSwordPiperMetadataPaths != null && !unitSwordPiperMetadataPaths.isEmpty()){
                                                        NodeServerId nodeServerId = ZkTreePathHelper.parseNodeServerMasterPath(unitSwordPiperPath);
                                                        nodeServerId.setType(nodeServerType);
                                                        NodeMetadataInfo nodeMetadataInfo = new NodeMetadataInfo();
                                                        for(String unitSwordPiperMetadataPath : unitSwordPiperMetadataPaths){

                                                            if(unitSwordPiperMetadataPath.endsWith(ZK_SWORD_PIPER_MASTER)){
                                                                List<String> masterRunningPaths =  zkClient.getChildren(unitSwordPiperMetadataPath);
                                                                if(masterRunningPaths != null && !masterRunningPaths.isEmpty()){
                                                                    for(String masterRunningPath : masterRunningPaths){
                                                                        if(masterRunningPath.endsWith(ZK_SWORD_PIPER_MASTER_RUNNING)){
                                                                            String masterInfo = zkClient.readData(masterRunningPath);
                                                                            NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(masterInfo);
                                                                            nodeServerInfo.setId(nodeServerId);
                                                                            nodeMetadataInfo.setInfo(nodeServerInfo);
                                                                        }
                                                                    }
                                                                }
                                                            }else if(unitSwordPiperMetadataPath.endsWith(ZK_SWORD_PIPER_DATA)){
                                                                List<String> consumeMetadataPaths =  zkClient.getChildren(unitSwordPiperMetadataPath);
                                                                if(consumeMetadataPaths != null && !consumeMetadataPaths.isEmpty()){
                                                                    Map<NodeServerId, NodeConsumptionInfo> consumeDataMap = new HashMap<>();
                                                                    for(String consumeMetadataPath : consumeMetadataPaths){
                                                                        String unitCategory2Name = consumeMetadataPath.substring(consumeMetadataPath.lastIndexOf("/")+1);
                                                                        String[] cNames = unitCategory2Name.split("|");

                                                                        String consumeData = zkClient.readData(consumeMetadataPath);
                                                                        NodeServerId consumeNodeServerId = new NodeServerId(dcName, cNames[0], cNames[1], serverName);
                                                                        consumeDataMap.put(consumeNodeServerId, new NodeConsumptionInfo(consumeNodeServerId, Long.parseLong(consumeData)));

                                                                    }
                                                                    nodeMetadataInfo.setConsumeData(consumeDataMap);
                                                                }
                                                            }else if(unitSwordPiperMetadataPath.endsWith(ZK_SWORD_PIPER_CONFIG)){
                                                                String nodeConfig = zkClient.readData(unitSwordPiperMetadataPath);
                                                                nodeMetadataInfo.setConfig(new NodeServerConfig(nodeConfig));
                                                            }
                                                            nodeMetadataInfo.setId(nodeServerId);
                                                            metadataInfoMap.put(nodeServerId, nodeMetadataInfo);
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

    private void registerDataListeners(){
            if(unitMetadataDic != null && !unitMetadataDic.isEmpty()){
                for (NodeServerId nodeServerId : unitMetadataDic.keySet()){
                    NodeMetadataInfo nodeMetadataInfo = unitMetadataDic.get(nodeServerId);
                    String nodeServerMasterRunningPath = ZkTreePathHelper.getNodeServerMasterRunningPath(nodeServerId);
                    zkClient.subscribeDataChanges(nodeServerMasterRunningPath, new IZkDataListener(){


                        @Override
                        public void handleDataChange(String dataPath, Object data) throws Exception {

                            NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(data.toString());
                            nodeServerInfo.setId(nodeServerId);
                            nodeMetadataInfo.setInfo(nodeServerInfo);

                            if(unitsMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent<>();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                                dataEvent.setData(nodeMetadataInfo);
                                unitsMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }

                        }

                        @Override
                        public void handleDataDeleted(String dataPath) throws Exception {
                            //维护内存数据
                            nodeMetadataInfo.setInfo(null);

                            //有监听器发送监听数据
                            if(unitsMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_DELETE);
                                dataEvent.setData(nodeMetadataInfo);
                                unitsMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }
                        }
                    });
                }
            }

            if(proxyUnitMetadataDic != null && !proxyUnitMetadataDic.isEmpty()){
                for (NodeServerId nodeServerId : proxyUnitMetadataDic.keySet()){
                    NodeMetadataInfo nodeMetadataInfo = proxyUnitMetadataDic.get(nodeServerId);
                    String nodeServerMasterRunningPath = ZkTreePathHelper.getNodeServerMasterRunningPath(nodeServerId);
                    zkClient.subscribeDataChanges(nodeServerMasterRunningPath, new IZkDataListener(){


                        @Override
                        public void handleDataChange(String dataPath, Object data) throws Exception {

                            NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(data.toString());
                            nodeServerInfo.setId(nodeServerId);
                            nodeMetadataInfo.setInfo(nodeServerInfo);

                            if(proxyUnitsMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent<>();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                                dataEvent.setData(nodeMetadataInfo);

                                proxyUnitsMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }
                        }

                        @Override
                        public void handleDataDeleted(String dataPath) throws Exception {
                            nodeMetadataInfo.setInfo(null);

                            if(proxyUnitsMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_DELETE);
                                dataEvent.setData(nodeMetadataInfo);
                                proxyUnitsMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }
                        }
                    });
                }
            }

            if(otherProxyUnitMetadataDic != null && !otherProxyUnitMetadataDic.isEmpty()){
                for (NodeServerId nodeServerId : otherProxyUnitMetadataDic.keySet()){
                    NodeMetadataInfo nodeMetadataInfo = otherProxyUnitMetadataDic.get(nodeServerId);
                    String nodeServerMasterRunningPath = ZkTreePathHelper.getNodeServerMasterRunningPath(nodeServerId);
                    zkClient.subscribeDataChanges(nodeServerMasterRunningPath, new IZkDataListener(){

                        @Override
                        public void handleDataChange(String dataPath, Object data) throws Exception {

                            NodeServerInfo nodeServerInfo = NodeServerInfoHelper.getNodeServerInfo(data.toString());
                            nodeServerInfo.setId(nodeServerId);
                            nodeMetadataInfo.setInfo(nodeServerInfo);

                            if(otherDcProxyMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent<>();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_CHANGE);
                                dataEvent.setData(nodeMetadataInfo);

                                otherDcProxyMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }
                        }

                        @Override
                        public void handleDataDeleted(String dataPath) throws Exception {

                            nodeMetadataInfo.setInfo(null);

                            if(otherDcProxyMetadataInfoListeners != null){
                                DataEvent<NodeMetadataInfo> dataEvent = new DataEvent();
                                dataEvent.setType(DataEventType.NODE_MASTER_DATA_DELETE);
                                dataEvent.setData(nodeMetadataInfo);

                                otherDcProxyMetadataInfoListeners.forEach(listenter->{
                                    listenter.listen(dataEvent);
                                });
                            }
                        }
                    });
                }
            }

    }

    /**
     * 获取指定的单元元数据存储
     * @param nodeServerId
     * @return
     */
    private Map<NodeServerId, NodeMetadataInfo> getNodeServerUnitMetadataDic(NodeServerId nodeServerId) {
        if(ZK_SWORD_UNITS.endsWith(nodeServerId.getUnitCategoryName())){
            return unitMetadataDic;
        }else if(ZK_SWORD_PROXY_UNITS.endsWith(nodeServerId.getUnitCategoryName())){
            return proxyUnitMetadataDic;
        }else if(ZK_SWORD_OTHER_PROXY_UNITS.endsWith(nodeServerId.getUnitCategoryName())){
            return otherProxyUnitMetadataDic;
        }
        return null;
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo>  getAllUnitMetadataInfo(NodeServerId nodeServerId) {
        return Collections.unmodifiableMap(unitMetadataDic);
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo> getAllUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> unitsMetadataInfoListener) {
        if(unitsMetadataInfoListener != null){
            this.unitsMetadataInfoListeners.add(unitsMetadataInfoListener);
        }
        return Collections.unmodifiableMap(unitMetadataDic);
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo> getAllProxyUnitMetadataInfo(NodeServerId nodeServerId) {
        return Collections.unmodifiableMap(proxyUnitMetadataDic);
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo> getAllProxyUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> proxyUnitsMetadataInfoListener) {
        if(proxyUnitsMetadataInfoListener != null){
            this.proxyUnitsMetadataInfoListeners.add(proxyUnitsMetadataInfoListener);
        }
        return Collections.unmodifiableMap(proxyUnitMetadataDic);
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo> getAllOtherProxyUnitMetadataInfo(NodeServerId nodeServerId) {
        return Collections.unmodifiableMap(otherProxyUnitMetadataDic);
    }

    @Override
    public Map<NodeServerId, NodeMetadataInfo> getAllOtherProxyUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> otherDcProxyMetadataInfoListener) {
        if(otherDcProxyMetadataInfoListener != null){
            this.otherDcProxyMetadataInfoListeners.add(otherDcProxyMetadataInfoListener);
        }
        return Collections.unmodifiableMap(otherProxyUnitMetadataDic);
    }

    @Override
    public NodeMetadataInfo getMetadataInfo(NodeServerId nodeServerId) {
        NodeMetadataInfo nodeMetadataInfo = null;
        Map<NodeServerId, NodeMetadataInfo> nodeServerUnitMetadataDic = getNodeServerUnitMetadataDic(nodeServerId);
        if(nodeServerUnitMetadataDic != null){
            nodeMetadataInfo = nodeServerUnitMetadataDic.get(nodeServerId);
        }
        return nodeMetadataInfo;
    }

    @Override
    public NodeMetadataInfo getMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> ownNodeServerConfigListener) {
        NodeMetadataInfo nodeMetadataInfo = getMetadataInfo(nodeServerId);
        if(ownNodeServerConfigListener == null){
            this.ownNodeServerConfigListener =  ownNodeServerConfigListener;
            String nodeServerConfigPath = ZkTreePathHelper.getNodeServerConfigPath(nodeServerId);
            zkClient.subscribeDataChanges(nodeServerConfigPath, new IZkDataListener(){

                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {

                    nodeMetadataInfo.setConfig(new NodeServerConfig(data.toString()));

                    if(ownNodeServerConfigListener != null){
                        DataEvent<NodeMetadataInfo> dataEvent = new DataEvent<>();
                        dataEvent.setType(DataEventType.NODE_CONFIG_DATA_CHANGE);
                        dataEvent.setData(nodeMetadataInfo);
                        ownNodeServerConfigListener.listen(dataEvent);
                    }
                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    nodeMetadataInfo.setConfig(null);

                    if(ownNodeServerConfigListener != null){
                        DataEvent<NodeMetadataInfo> dataEvent = new DataEvent();
                        dataEvent.setType(DataEventType.NODE_CONFIG_DATA_DELETE);
                        dataEvent.setData(nodeMetadataInfo);
                        ownNodeServerConfigListener.listen(dataEvent);
                    }
                }
            });
        }
        return nodeMetadataInfo;
    }

    @Override
    public boolean setNodeServerMetadataMasterInfo(NodeServerId nodeServerId, NodeServerInfo nodeServerInfo) {
        String masterRunningPath = ZkTreePathHelper.getNodeServerMasterRunningPath(nodeServerId);
        if(zkClient.exists(masterRunningPath)){
            return false;
        }
        zkClient.createEphemeral(masterRunningPath, NodeServerInfoHelper.getNodeServerInfoData(nodeServerInfo));
        return true;
    }

    @Override
    public void setNodeServerMetadataConsumeUnitData(NodeServerId nodeServerId, NodeConsumptionInfo nodeConsumptionInfo) {
        NodeMetadataInfo nodeMetadataInfo = getMetadataInfoEdited(nodeServerId);
        nodeMetadataInfo.getConsumeData().put(nodeConsumptionInfo.getId(), nodeConsumptionInfo);
        String consumeUnitPath = ZkTreePathHelper.getConsumeOtherNodeServerDataPath(nodeServerId, nodeConsumptionInfo.getId().getUnitCategoryName(),
                nodeConsumptionInfo.getId().getUnitName());
        if(!zkClient.exists(consumeUnitPath)){
            zkClient.createPersistent(consumeUnitPath, true);
        }
        //zk修改
        zkClient.writeData(consumeUnitPath, nodeConsumptionInfo.getDataItemId());
    }

    /**
     * 获取可写的元数据
     * @param nodeServerId
     * @return
     */
    private NodeMetadataInfo getMetadataInfoEdited(NodeServerId nodeServerId) {
        NodeMetadataInfo nodeMetadataInfo = null;
        Map<NodeServerId, NodeMetadataInfo> nodeServerUnitMetadataDic = getNodeServerUnitMetadataDic(nodeServerId);
        if(nodeServerUnitMetadataDic != null){
            nodeMetadataInfo = nodeServerUnitMetadataDic.get(nodeServerId);
        }
        return nodeMetadataInfo;
    }
}
