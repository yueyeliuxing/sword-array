package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.mq.jade.embedded.EmbeddedBroker;
import com.zq.sword.array.mq.jade.producer.Producer;
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

    private EmbeddedBroker broker;

    public EmbeddedSlaveRedisReplicator(String uri, String topic,EmbeddedBroker broker, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(uri, topic, cycleDisposeHandler);
        this.broker =  broker;
    }

    @Override
    protected Producer createProducer() {
        return broker.createProducer();
    }
}
