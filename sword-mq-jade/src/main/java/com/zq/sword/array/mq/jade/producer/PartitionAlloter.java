package com.zq.sword.array.mq.jade.producer;

/**
 * @program: sword-array
 * @description: 分片分派器
 * @author: zhouqi1
 * @create: 2019-03-14 16:05
 **/
public interface PartitionAlloter {

    /**
     * 分配分片资源
     * @param topic
     * @return
     */
    ProducePartition allotPartition(String topic);
}
