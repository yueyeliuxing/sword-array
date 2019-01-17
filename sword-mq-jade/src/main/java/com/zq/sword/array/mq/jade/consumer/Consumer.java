package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: 消费者
 * @author: zhouqi1
 * @create: 2019-01-17 11:17
 **/
public interface Consumer extends Actuator {

    /**
     * 设置分组
     * @param groupName
     */
    void group(String groupName);

    /**
     * 监听的 topic
     * @param topics
     */
    void listenTopic(String... topics);

    /**
     * 注册事件监听器
     * @param messageListener
     */
    void register(MessageListener messageListener);

}
