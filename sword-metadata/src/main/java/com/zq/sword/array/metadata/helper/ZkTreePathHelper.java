package com.zq.sword.array.metadata.helper;

import com.zq.sword.array.common.node.NodeServerId;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkTreePathHelper {

    private static final String ZK_ROOT = "/sword";

    private static final String ZK_SWORD_UNITS= "/units";

    private static final String ZK_SWORD_PIPER = "/piper";

    private static final String ZK_SWORD_PIPER_TYPE = "/type";

    private static final String ZK_SWORD_PIPER_MASTER = "/master/running";

    private static final String ZK_SWORD_PIPER_SLAVE = "/slave";

    private static final String ZK_SWORD_PIPER_DATA = "/data";

    private static final String ZK_SWORD_PIPER_CONFIG = "/config";

    public static String getRealPath(String path) {
        return ZK_ROOT+path;
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
        nodeServerId.setUnitName(pathItems[2]);
        nodeServerId.setServerName(pathItems[4]);
        return nodeServerId;
    }

    /**
     * 得到节点对应的类型节点路径
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerTypePath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s/%s%s/%s%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_TYPE));
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
     * 转换得到piper服务的slave
     * @param nodeServerId
     * @return
     */
    public static String getNodeServerSlavePath(NodeServerId nodeServerId) {
        return getRealPath(String.format("/%s/%s%s/%s%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_SLAVE));
    }

    /**
     * 转换得到piper服务的Master
     * @param nodeServerId
     * @return
     */
    public static String getConsumeOtherNodeServerDataPath(NodeServerId nodeServerId, String otherUnitName) {
        return getRealPath(String.format("/%s/%s%s/%s%s/%s/%s", nodeServerId.getDcName(),nodeServerId.getUnitName(),
                ZK_SWORD_PIPER, nodeServerId.getServerName(), ZK_SWORD_PIPER_DATA, otherUnitName, nodeServerId.getServerName()));
    }

}
