package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: 消息调度器
 * @author: zhouqi1
 * @create: 2019-01-18 19:31
 **/
public interface ConsumeDispatcher extends Actuator{

    /**
     * 创建消费者
     * @return
     */
    Consumer createConsumer(String[] topics, String group);
}
