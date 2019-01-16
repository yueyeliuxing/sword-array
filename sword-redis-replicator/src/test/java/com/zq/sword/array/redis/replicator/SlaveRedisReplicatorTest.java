package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.mq.jade.bitcask.BitcaskRightRandomQueue;
import org.junit.Test;

public class SlaveRedisReplicatorTest {

    private SlaveRedisReplicator slaveRedisReplicator;

    @Test
    public void start() {
        //初始化R-queue
        String rightDataFilePath = "E:\\sword\\right\\data";
        String rightIndexFilePath = "E:\\sword\\right\\index";
        RightRandomQueue<SwordData> rightRandomQueue = new BitcaskRightRandomQueue(rightDataFilePath, rightIndexFilePath);

        //初始化redis slave replicator
        String redisUri = "redis://127.0.0.1:6379";
        slaveRedisReplicator = SwordSlaveRedisReplicator.SwordSlaveRedisReplicatorBuilder.create()
                .idGenerat(0, 0)
                .listen(redisUri)
                .bindingDataSource(rightRandomQueue)
                .build();
        slaveRedisReplicator.start();
    }
}