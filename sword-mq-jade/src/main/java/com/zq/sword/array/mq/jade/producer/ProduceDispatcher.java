package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: 生产调度器
 * @author: zhouqi1
 * @create: 2019-01-17 15:33
 **/
public interface ProduceDispatcher extends Actuator {

    /**
     * 分配选择topic 下的一个分片
     * @param topic
     * @return
     */
    PartitionResource allotPartition(String topic);
    /**
     * 创建生产者
     * @return
     */
    Producer createProducer();

}
