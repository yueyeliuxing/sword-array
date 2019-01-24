package com.zq.sword.array.mq.jade.embedded;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.consumer.AbstractConsumer;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;

/**
 * @program: sword-array
 * @description: 容器消费者
 * @author: zhouqi1
 * @create: 2019-01-18 13:22
 **/
public class EmbeddedConsumer extends AbstractConsumer implements Consumer {

    private Broker broker;

    public EmbeddedConsumer(Broker broker, NameCoordinator coordinator, String[] topics, String group) {
        super(coordinator, topics, group);
        this.broker = broker;
    }

    public EmbeddedConsumer(Broker broker, NameCoordinator coordinator) {
        super(coordinator);
        this.broker = broker;
    }

    @Override
    protected Partition createRpcPartition(NameDuplicatePartition duplicateNamePartition) {
        if(broker.contains(duplicateNamePartition.getId())){
            return null;
        }
        return super.createRpcPartition(duplicateNamePartition);
    }
}