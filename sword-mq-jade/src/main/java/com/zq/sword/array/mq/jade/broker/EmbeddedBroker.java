package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.producer.Producer;

/**
 * @program: sword-array
 * @description: 嵌入式broker
 * @author: zhouqi1
 * @create: 2019-01-23 19:08
 **/
public interface EmbeddedBroker extends Broker {


    /**
     * 设置topic
     * @param topics
     */
    void topics(String... topics);

    /**
     * 设置tag
     * @param tag
     */
    void tag(String tag);

    /**
     * 创建生产者
     * @return
     */
    Producer createProducer();

    /**
     * 创建消费者
     * @return
     */
    Consumer createConsumer(String group);
}
