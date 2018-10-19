package com.zq.sword.array.node.server;

import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.data.rqueue.bitcask.BitcaskRightRandomQueue;
import com.zq.sword.array.redis.slave.SwordSlaveRedisReplicator;
import com.zq.sword.array.redis.slave.SlaveRedisReplicator;

/**
 * @program: sword-array
 * @description: 服务启动器
 * @author: zhouqi1
 * @create: 2018-10-19 10:15
 **/
public class SwordServerStarter {

    public void start(){
        //1.zk获取参数

        //初始化R-queue
        String rightDataFilePath = "E:\\sword\\right\\data";
        String rightIndexFilePath = "E:\\sword\\right\\index";
        RightRandomQueue rightRandomQueue = new BitcaskRightRandomQueue(rightDataFilePath, rightIndexFilePath);

        //初始化redis slave replicator
        String redisUri = "redis://127.0.0.1:6379";
        SlaveRedisReplicator slaveRedisReplicator = new SwordSlaveRedisReplicator(redisUri, rightRandomQueue);
        slaveRedisReplicator.start();
    }
}
