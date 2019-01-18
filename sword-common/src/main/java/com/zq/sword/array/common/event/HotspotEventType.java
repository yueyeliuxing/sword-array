package com.zq.sword.array.common.event;

/**
 * @program: sword-array
 * @description: 数据改变事件
 * @author: zhouqi1
 * @create: 2018-07-23 17:24
 **/
public enum HotspotEventType {

    /**
     * 节点配置数据修改
     */
    NODE_CONFIG_DATA_CHANGE,

    /**
     * 节点配置删除
     */
    NODE_CONFIG_DATA_DELETE,

    /**
     * 节点Master running 临时节点数据修改
     */
    NODE_MASTER_DATA_CHANGE,

    /**
     * 节点Master running 临时节点删除
     */
    NODE_MASTER_DATA_DELETE,

    /**
     * 节点Master 启动成功
     */
    NODE_MASTER_STATED,

    /**
     * 节点数据改变
     */
    NODE_DATA_ITEM_CHANGE,

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

    ;
}
