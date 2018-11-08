package com.zq.sword.array.metadata.data;

import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.metadata.data.NodeId;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkTreePathBuilder {

    public static final String ZK_ROOT = "/p-piper";

    public static final String ZK_SWORD_UNITS= "/units";

    public static final String ZK_SWORD_PROXY_UNITS= "/own-proxy-unit";

    public static final String ZK_SWORD_OTHER_PROXY_UNITS= "/other-proxy-units";

    public static final String ZK_SWORD_PIPER = "/piper";

    public static final String ZK_SWORD_PIPER_MASTER = "/master";

    public static final String ZK_SWORD_PIPER_MASTER_RUNNING = "/running";

    public static final String ZK_SWORD_PIPER_DATA = "/data";

    public static final String ZK_SWORD_PIPER_CONFIG = "/config";

    public static String getRealPath(String path) {
        return ZK_ROOT+path;
    }

    /**
     * 获取服务节点的父路径
     * @param nodeId
     * @return
     */
    public static String getNodeServerPath(NodeServerId nodeId){
        return getRealPath(String.format("/%s/%s/%s%s/%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),
                nodeId.getUnitName(), ZK_SWORD_PIPER, nodeId.getServerName()));
    }

    /**
     * 解析节点路径获取nodeServerId
     * @param nodeServerMasterPath
     * @return
     */
    public static NodeId parseNodeServerMasterPath(String nodeServerMasterPath) {
        if(nodeServerMasterPath == null) {
            throw new NullPointerException("nodeServerMasterPath");
        }
        String[] pathItems = nodeServerMasterPath.split("/");
        NodeId nodeId = new NodeId();
        nodeId.setDcName(pathItems[1]);
        nodeId.setUnitCategoryName(pathItems[2]);
        nodeId.setUnitName(pathItems[3]);
        nodeId.setServerName(pathItems[5]);

        if(ZK_SWORD_UNITS.endsWith(nodeId.getUnitCategoryName())){
            nodeId.setType(NodeType.DC_UNIT_PIPER);
        }else if(ZK_SWORD_PROXY_UNITS.endsWith(nodeId.getUnitCategoryName())){
            nodeId.setType(NodeType.DC_UNIT_PROXY_PIPER);
        }else if(ZK_SWORD_OTHER_PROXY_UNITS.endsWith(nodeId.getUnitCategoryName())){
            nodeId.setType(NodeType.OTHER_DC_UNIT_PROXY_PIPER);
        }
        return nodeId;
    }


    /**
     * 转换得到piper服务的启动配置项
     * @param nodeId
     * @return
     */
    public static String buildNodeConfigPath(NodeId nodeId) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),
                nodeId.getUnitName(), ZK_SWORD_PIPER, nodeId.getServerName(), ZK_SWORD_PIPER_CONFIG));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeId
     * @return
     */
    public static String getNodeServerMasterPath(NodeServerId nodeId) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),
                nodeId.getUnitName(), ZK_SWORD_PIPER, nodeId.getServerName(), ZK_SWORD_PIPER_MASTER));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeId
     * @return
     */
    public static String buildNodeServerMasterRunningPath(NodeId nodeId) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),
                nodeId.getUnitName(), ZK_SWORD_PIPER, nodeId.getServerName(), ZK_SWORD_PIPER_MASTER, ZK_SWORD_PIPER_MASTER_RUNNING));
    }


    /**
     * 转换得到piper服务的Master
     * @param nodeId
     * @return
     */
    public static String buildConsumedNodeDataPath(NodeId nodeId, NodeId consumeNodeId) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s/%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),nodeId.getUnitName(),
                ZK_SWORD_PIPER, nodeId.getServerName(), ZK_SWORD_PIPER_DATA, NodeIdBuilder.toNodeIdString(consumeNodeId)));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeId
     * @return
     */
    public static String buildConsumedNodeDataParentPath(NodeId nodeId) {
        return getRealPath(String.format("/%s/%s/%s%s/%s%s", nodeId.getDcName(),nodeId.getUnitCategoryName(),nodeId.getUnitName(),
                ZK_SWORD_PIPER, nodeId.getServerName(), ZK_SWORD_PIPER_DATA));
    }

}
