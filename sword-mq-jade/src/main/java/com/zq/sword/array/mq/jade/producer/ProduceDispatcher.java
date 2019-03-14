package com.zq.sword.array.mq.jade.producer;

/**
 * @program: sword-array
 * @description: 生产调度器
 * @author: zhouqi1
 * @create: 2019-01-17 15:33
 **/
public interface ProduceDispatcher {

    /**
     * 创建生产者
     * @return
     */
    Producer createGeneralProducer();

    /**
     * 创建生产者
     * @param selectStrategy
     * @return
     */
    Producer createGeneralProducer(PartitionSelectStrategy selectStrategy);

}
