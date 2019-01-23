package com.zq.sword.array.mq.jade.embedded;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.consumer.AbstractConsumeDispatcher;
import com.zq.sword.array.mq.jade.consumer.ConsumeDispatcher;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;

/**
 * @program: sword-array
 * @description: broker的消费调度器
 * @author: zhouqi1
 * @create: 2019-01-18 20:26
 **/
public class EmbeddedConsumeDispatcher extends AbstractConsumeDispatcher implements ConsumeDispatcher {

    private Broker broker;

    public EmbeddedConsumeDispatcher(NameCoordinator coordinator, Broker broker) {
        super(coordinator);
        this.broker = broker;
    }

    @Override
    protected Consumer doCreateConsumer(String[] topics, String group) {
        return  new EmbeddedConsumer(broker, coordinator, topics, group);
    }
}
