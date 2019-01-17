package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.msg.Message;

/**
 * @program: sword-array
 * @description: 消息监听器
 * @author: zhouqi1
 * @create: 2019-01-17 12:44
 **/
public interface MessageListener {

    /**
     * 消费消息
     * @param message
     * @return
     */
    ConsumeStatus consume(Message message);
}
