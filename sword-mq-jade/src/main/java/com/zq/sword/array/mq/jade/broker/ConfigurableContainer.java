package com.zq.sword.array.mq.jade.broker;

/**
 * @program: sword-array
 * @description: 可配置的容器
 * @author: zhouqi1
 * @create: 2019-01-16 15:38
 **/
public interface ConfigurableContainer extends Container {

    /**
     * 设置ID
     * @param id
     */
    void setId(long id);

    /**
     * 放入分片
     * @param partId
     * @param partition
     */
    void put(long partId, Partition partition);
}
