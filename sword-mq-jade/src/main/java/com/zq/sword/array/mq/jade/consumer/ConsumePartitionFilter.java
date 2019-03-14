package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;

/**
 * 消费分片过滤器
 */
public interface ConsumePartitionFilter {

    /**
     * 过滤
     * @param partition
     * @return
     */
    boolean filter(NameDuplicatePartition partition);
}
