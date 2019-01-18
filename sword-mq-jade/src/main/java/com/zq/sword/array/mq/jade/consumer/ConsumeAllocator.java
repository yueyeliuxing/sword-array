package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: 消费者协调分配器
 * @author: zhouqi1
 * @create: 2019-01-18 10:14
 **/
public interface ConsumeAllocator extends Actuator {

    /**
     *
     * @param group
     */
    void group(String group);

    /**
     * 设置指定的topic
     * @param topic
     */
    void topic(String topic);

}
