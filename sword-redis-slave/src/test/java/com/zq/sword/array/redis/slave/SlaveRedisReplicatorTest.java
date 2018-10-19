package com.zq.sword.array.redis.slave;


import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.data.rqueue.bitcask.BitcaskRightRandomQueue;
import org.junit.Before;
import org.junit.Test;

public class SlaveRedisReplicatorTest {

    private SlaveRedisReplicator slaveRedisReplicator;

    @Test
    public void start() {
        //初始化R-queue
        String rightDataFilePath = "E:\\sword\\right\\data";
        String rightIndexFilePath = "E:\\sword\\right\\index";
        RightRandomQueue rightRandomQueue = new BitcaskRightRandomQueue(rightDataFilePath, rightIndexFilePath);

        //初始化redis slave replicator
        String redisUri = "redis://127.0.0.1:6379";
        slaveRedisReplicator = new SwordSlaveRedisReplicator(redisUri, rightRandomQueue);
        slaveRedisReplicator.start();
    }
}