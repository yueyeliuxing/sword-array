package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;

import java.util.List;

/**
 * @program: sword-array
 * @description: 分片选择策略
 * @author: zhouqi1
 * @create: 2019-01-17 15:41
 **/
public interface PartitionSelectStrategy {

    /**
     *
     * @param partitions
     * @return
     */
    ProducePartition select(List<NameDuplicatePartition> partitions);
}
