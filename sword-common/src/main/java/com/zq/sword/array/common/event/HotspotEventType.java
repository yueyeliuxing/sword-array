package com.zq.sword.array.common.event;

/**
 * @program: sword-array
 * @description: 数据改变事件
 * @author: zhouqi1
 * @create: 2018-07-23 17:24
 **/
public enum HotspotEventType {

    /**
     * 数据添加
     */
    SWORD_DATA_ADD,

    /**
     * 数据删除
     */
    SWORD_DATA_DEL,

    /**
     * 消费者节点改变
     */
    CONSUMER_NODE_CHANGE,

    /**
     * 分片节点改变
     */
    PARTITION_NODE_CHANGE,

    /**
     * 分片节点删除
     */
    PARTITION_NODE_DEL,

    /**
     * 消费者要消费的分片信息发生改变
     */
    CONSUME_PARTITION_DATA_CHANGE,

    /**
     * 消费分配节点删除
     */
    CONSUME_ALLOCATOR_NODE_DEL,

    /**
     * piper master 节点删除
     */
    PIPER_MASTER_NODE_DEL,

    ;
}
