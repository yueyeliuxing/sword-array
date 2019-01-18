package com.zq.sword.array.redis.replicator;


import org.junit.Test;

public class SlaveRedisReplicatorTest {

    private SlaveRedisReplicator slaveRedisReplicator;

    @Test
    public void start() {

        //初始化redis slave replicator
        String redisUri = "redis://127.0.0.1:6379";
        slaveRedisReplicator = new EmbeddedSlaveRedisReplicator(redisUri, "awdwq", null, null);
        slaveRedisReplicator.start();
    }
}