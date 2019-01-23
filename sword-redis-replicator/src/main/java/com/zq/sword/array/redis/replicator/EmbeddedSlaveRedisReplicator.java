package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.embedded.EmbeddedProduceDispatcher;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class EmbeddedSlaveRedisReplicator extends AbstractSlaveRedisReplicator implements SlaveRedisReplicator {

    private Logger logger = LoggerFactory.getLogger(EmbeddedSlaveRedisReplicator.class);

    public EmbeddedSlaveRedisReplicator(String uri, String topic, NameCoordinator coordinator, Broker broker, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(uri, topic, new EmbeddedProduceDispatcher(coordinator, broker), cycleDisposeHandler);
    }
}
