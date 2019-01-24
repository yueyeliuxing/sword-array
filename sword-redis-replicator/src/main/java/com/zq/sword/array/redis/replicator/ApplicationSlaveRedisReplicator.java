package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.mq.jade.producer.DefaultProduceDispatcher;
import com.zq.sword.array.mq.jade.producer.ProduceDispatcher;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.redis.handler.RedisCycleDisposeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class ApplicationSlaveRedisReplicator extends AbstractSlaveRedisReplicator implements SlaveRedisReplicator {

    private Logger logger = LoggerFactory.getLogger(ApplicationSlaveRedisReplicator.class);

    private ProduceDispatcher produceDispatcher;

    public ApplicationSlaveRedisReplicator(String uri, String topic, String connectAddr) throws URISyntaxException {
        super(uri, topic, new RedisCycleDisposeHandler(new URI(uri).getHost(), new URI(uri).getPort(), null));
        this.produceDispatcher = new DefaultProduceDispatcher(connectAddr);
    }

    @Override
    protected Producer createProducer() {
        return produceDispatcher.createProducer();
    }
}
