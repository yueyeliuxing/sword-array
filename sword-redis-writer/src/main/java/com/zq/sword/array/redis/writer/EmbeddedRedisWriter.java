package com.zq.sword.array.redis.writer;

import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.embedded.EmbeddedBroker;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:43
 **/
public class EmbeddedRedisWriter extends AbstractRedisWriter implements RedisWriter {

    private Logger logger = LoggerFactory.getLogger(EmbeddedRedisWriter.class);

    /**
     * 消费调度器
     */
    private EmbeddedBroker broker;

    public EmbeddedRedisWriter(RedisConfig redisConfig, String fileLocation, EmbeddedBroker broker,CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(redisConfig, fileLocation, cycleDisposeHandler);
        this.broker = broker;
    }

    @Override
    public Consumer createConsumer() {
        return broker.createConsumer(broker.id()+"group");
    }

}
