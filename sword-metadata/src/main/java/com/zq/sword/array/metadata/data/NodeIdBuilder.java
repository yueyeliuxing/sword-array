package com.zq.sword.array.metadata.data;

/**
 * @program: sword-array
 * @description: NodeNamingInfo 构建类
 * @author: zhouqi1
 * @create: 2018-07-24 16:44
 **/
public class NodeIdBuilder {

    /**
     * 转换为数据
     * @param nodeId
     * @return
     */
    public static String toNodeIdString(NodeId nodeId) {
        return String.format("%s|%s|%s|%s|%s", nodeId.getType().name(), nodeId.getDc(), nodeId.getUnitCategory(),
                nodeId.getUnit(), nodeId.getGroup());
    }

    /**
     * 转换为数据
     * @param nodeIdString
     * @return
     */
    public static NodeId buildNodeId(String nodeIdString) {
        if(nodeIdString != null) {
            String[] items = nodeIdString.split("\\|");
            if(items.length != 5) {
                return null;
            }
            return new NodeId(NodeType.valueOf(items[0]), items[1], items[2], items[3], items[4]);
        }
        return null;
    }
}
