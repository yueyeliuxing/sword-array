package com.zq.sword.array.metadata.helper;

import com.zq.sword.array.common.node.NodeServerId;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkTreePathHelper {

    public static final String ZK_ROOT = "/sword";

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
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerPath(NodeServerId nodeServerId){
        return getRealPath(String.format("/%s/%s/%s%s/%s", nodeServerId.getDcName(),nodeServerId.getUnitCategoryName(),
                nodeServerId.getUnitName(), ZK_SWORD_PIPER, nodeServerId.getServerName()));
    }

    /**
     * 获取所有单元的名称
     * @param nodeServerId
     * @return
     */
    public static String getUnitNamePath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s%s", nodeServerId.getDcName(), ZK_SWORD_UNITS));
    }

    /**
     * 解析节点路径获取nodeServerId
     * @param nodeServerMasterPath
     * @return
     */
    public static NodeServerId parseNodeServerMasterPath(String nodeServerMasterPath) {
        if(nodeServerMasterPath == null) {
            throw new NullPointerException("nodeServerMasterPath");
        }
        String[] pathItems = nodeServerMasterPath.split("/");
        NodeServerId nodeServerId = new NodeServerId();
        nodeServerId.setDcName(pathItems[1]);
        nodeServerId.setUnitCategoryName(pathItems[2]);
        nodeServerId.setUnitName(pathItems[3]);
        nodeServerId.setServerName(pathItems[5]);
        return nodeServerId;
    }


    /**
     * 转换得到piper服务的启动配置项
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerConfigPath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s/%s%s/%s%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_CONFIG));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerMasterPath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s/%s%s/%s%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_MASTER));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerMasterRunningPath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s/%s%s/%s%s%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_MASTER, ZK_SWORD_PIPER_MASTER_RUNNING));
    }
    /**
     *  parentPath 获取master running节点
     * @param nodeServerParentPath
     * @return
     */
    public static String getNodeServerMasterRunningPath(String nodeServerParentPath) {
        return getRealPath(String.format("%s%s%s", nodeServerParentPath, ZK_SWORD_PIPER_MASTER, ZK_SWORD_PIPER_MASTER_RUNNING));
    }


    /**
     * 转换得到piper服务的Master
     * @param nodeServerId
     * @return
     */
    public static String getConsumeOtherNodeServerDataPath(NodeServerId nodeServerId, String otherUnitCategoryName, String otherUnitName) {
        return getRealPath(String.format("/%s/%s%s/%s%s/%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_DATA, String.format("%s|%s", otherUnitCategoryName, otherUnitName)));
    }

}
