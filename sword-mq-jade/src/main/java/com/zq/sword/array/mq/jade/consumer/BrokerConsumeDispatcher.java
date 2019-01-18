package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;

/**
 * @program: sword-array
 * @description: broker的消费调度器
 * @author: zhouqi1
 * @create: 2019-01-18 20:26
 **/
public class BrokerConsumeDispatcher extends AbstractConsumeDispatcher implements ConsumeDispatcher{

    private Broker broker;

    public BrokerConsumeDispatcher(Broker broker, NameCoordinator coordinator) {
        super(coordinator);
        this.broker = broker;
    }


    @Override
    public Consumer createConsumer() {
        return new BrokerConsumer(broker, coordinator, topics, group);
    }
}
