package com.zq.sword.array.mq.jade.consumer;

/**
 * @program: sword-array
 * @description: 消息调度器
 * @author: zhouqi1
 * @create: 2019-01-18 19:31
 **/
public interface ConsumeDispatcher {

    /**
     * 创建消费者
     * @return
     */
    Consumer createDefaultConsumer(String[] topics, String group);

    /**
     * 创建消费者
     * @return
     */
    Consumer createDefaultConsumer(String[] topics, String group, ConsumePartitionFilter partitionFilter);

}
