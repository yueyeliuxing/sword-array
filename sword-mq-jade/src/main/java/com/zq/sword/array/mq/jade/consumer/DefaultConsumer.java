package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;

/**
 * @program: sword-array
 * @description: 默认的消息消费者
 * @author: zhouqi1
 * @create: 2019-01-18 13:28
 **/
public class DefaultConsumer extends AbstractConsumer implements Consumer {


    public DefaultConsumer(String connectAddr, int sessionTimeOut) {
        super(new ZkNameCoordinator(connectAddr, sessionTimeOut));
    }
}
