package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.msg.Message;

/**
 * @program: sword-array
 * @description: 生产者
 * @author: zhouqi1
 * @create: 2019-01-16 17:05
 **/
public interface Producer {

    /**
     * 发送消息
     * @param message
     * @return
     */
    boolean sendMsg(Message message);

}
