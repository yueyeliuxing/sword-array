package com.zq.sword.array.metadata.data;

import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.metadata.data.NodeNamingInfo;

/**
 * @program: sword-array
 * @description: NodeNamingInfo 构建类
 * @author: zhouqi1
 * @create: 2018-07-24 16:44
 **/
public class NodeNamingInfoBuilder {

    /**
     * 转换为数据
     * @param nodeNamingInfo
     * @return
     */
    public static String toNodeNamingInfoString(NodeNamingInfo nodeNamingInfo) {
        return String.format("%s:%s:%s", nodeNamingInfo.getHost(), nodeNamingInfo.getPort(), nodeNamingInfo.getBackupPort());
    }

    /**
     * 转换为数据
     * @param nodeNamingInfoString
     * @return
     */
    public static NodeNamingInfo buildNodeNamingInfo(String nodeNamingInfoString) {
        if(nodeNamingInfoString != null) {
            String[] items = nodeNamingInfoString.split(":");
            if(items.length != 3) {
                return null;
            }
            return new NodeNamingInfo(items[0], Integer.parseInt(items[1]), Integer.parseInt(items[2]));
        }
        return null;
    }
}
