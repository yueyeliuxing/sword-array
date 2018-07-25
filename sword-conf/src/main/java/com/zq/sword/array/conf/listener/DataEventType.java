package com.zq.sword.array.conf.listener;

/**
 * @program: sword-array
 * @description: 数据改变事件
 * @author: zhouqi1
 * @create: 2018-07-23 17:24
 **/
public enum DataEventType {

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
     * 其他节点Master running 临时节点数据修改
     */
    NODE_OTHER_MASTER_DATA_CHANGE,

    /**
     * 其他节点Master running 临时节点删除
     */
    NODE_OTHER_MASTER_DATA_DELETE,

    /**
     * 当前Master节点的slava节点变化
     */
    NODE_MASTER_SLAVE_CHANGE,
    ;
}
