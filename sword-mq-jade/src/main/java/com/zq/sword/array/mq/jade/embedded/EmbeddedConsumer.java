package com.zq.sword.array.mq.jade.embedded;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.consumer.AbstractConsumer;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 容器消费者
 * @author: zhouqi1
 * @create: 2019-01-18 13:22
 **/
public class EmbeddedConsumer extends AbstractConsumer implements Consumer {

    private Logger logger = LoggerFactory.getLogger(EmbeddedConsumer.class);

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
    protected boolean beforeCreateRpcPartition(NameDuplicatePartition duplicateNamePartition) {
        if(broker.contains(duplicateNamePartition.getId())){
            logger.info("如果是本地分片就返回为null, partId->{}", duplicateNamePartition.getId());
            return false;
        }
        return true;
    }
}
