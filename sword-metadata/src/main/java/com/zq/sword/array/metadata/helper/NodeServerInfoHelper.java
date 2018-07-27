package com.zq.sword.array.metadata.helper;

import com.zq.sword.array.common.node.NodeServerInfo;

/**
 * @program: sword-array
 * @description: NodeServerInfo帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 16:44
 **/
public class NodeServerInfoHelper {

    /**
     * 转换为数据
     * @param nodeServerInfo
     * @return
     */
    public static String getNodeServerInfoData(NodeServerInfo nodeServerInfo) {
        return String.format("%s:%s", nodeServerInfo.getServerAddress(), nodeServerInfo.getPort());
    }

    /**
     * 转换为数据
     * @param nodeServerInfoData
     * @return
     */
    public static NodeServerInfo getNodeServerInfo(String nodeServerInfoData) {
        if(nodeServerInfoData != null) {
            String[] items = nodeServerInfoData.split(":");
            if(items.length != 2) {
                return null;
            }
            return new NodeServerInfo(items[0], Integer.parseInt(items[1]));
        }
        return null;
    }
}
