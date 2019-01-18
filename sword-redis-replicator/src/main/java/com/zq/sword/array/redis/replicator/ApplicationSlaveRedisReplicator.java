package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.mq.jade.producer.DefaultProduceDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class ApplicationSlaveRedisReplicator extends AbstractSlaveRedisReplicator implements SlaveRedisReplicator {

    private Logger logger = LoggerFactory.getLogger(ApplicationSlaveRedisReplicator.class);

    public ApplicationSlaveRedisReplicator(String uri, String topic, String connectAddr) {
        super(uri, topic, new DefaultProduceDispatcher(connectAddr));
    }
}
