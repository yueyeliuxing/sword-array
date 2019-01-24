package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;

/**
 * @program: sword-array
 * @description: 默认的消费调度器
 * @author: zhouqi1
 * @create: 2019-01-18 20:26
 **/
public class DefaultConsumeDispatcher extends AbstractConsumeDispatcher implements ConsumeDispatcher{

    public DefaultConsumeDispatcher(String connectAddr) {
        super(new ZkNameCoordinator(connectAddr));
    }

    public DefaultConsumeDispatcher(NameCoordinator coordinator) {
        super(coordinator);
    }

    @Override
    protected Consumer doCreateConsumer(String[] topics, String group) {
        return new DefaultConsumer(coordinator, topics, group);
    }
}
