package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: 生产者
 * @author: zhouqi1
 * @create: 2019-01-17 14:34
 **/
public interface Producer extends Actuator {

    /**
     * 发送消息
     * @param message
     * @return
     */
    boolean sendMsg(Message message);
}
