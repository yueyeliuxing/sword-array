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
     * @param group
     */
    void group(String group);

    /**
     * 监听的 topic
     * @param topics
     */
    void listenTopic(String... topics);

    /**
     * 绑定事件监听器
     * @param messageListener
     */
    void bindingMessageListener(MessageListener messageListener);

}
