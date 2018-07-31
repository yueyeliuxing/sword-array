package com.zq.sword.array.common.node;

import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 元数据
 * @author: zhouqi1
 * @create: 2018-07-27 19:24
 **/
@Data
@ToString
public class NodeMetadataInfo {

    /**
     * id
     */
    private NodeServerId id;

    /**
     * master ip port信息
     */
    private NodeServerInfo info;

    /**
     * 消费信息
     */
    private Map<NodeServerId, NodeConsumptionInfo> consumeData = new ConcurrentHashMap<>();

    /**
     * 节点配置信息
     */
    private NodeServerConfig config;
}
