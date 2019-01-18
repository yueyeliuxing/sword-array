package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.tasks.Task;

/**
 * @program: sword-array
 * @description: 消费者协调分配器
 * @author: zhouqi1
 * @create: 2019-01-18 10:14
 **/
public interface ConsumeAllocator extends Task {

    /**
     * 设置指定的topic 集合
     * @param topics
     */
    void topics(String... topics);


    /**
     * 重新分配消费的分片信息
     */
    void reallocateConsumePartition();

}
