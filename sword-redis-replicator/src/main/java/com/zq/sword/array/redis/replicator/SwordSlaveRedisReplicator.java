package com.zq.sword.array.redis.replicator;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.common.data.SwordCommandSerializer;
import com.zq.sword.array.common.data.SwordData;
import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.metadata.data.SwordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class SwordSlaveRedisReplicator implements SlaveRedisReplicator<SwordCommand> {

    private Logger logger = LoggerFactory.getLogger(SwordSlaveRedisReplicator.class);

    private Replicator replicator;

    private RightRandomQueue<SwordData> rightRandomQueue;

    private IdGenerator idGenerator;

    public SwordSlaveRedisReplicator(SwordConfig swordConfig) {
        logger.info("SwordSlaveRedisReplicator start...");
        long workerId = swordConfig.getProperty("worker.id", Long.class);
        long datacenterId = swordConfig.getProperty("data.center.id", Long.class);
        idGenerator = new SnowFlakeIdGenerator(workerId, datacenterId);
        try {
            String uri = swordConfig.getProperty("redis.master.uri");
            replicator = new RedisReplicator(uri);
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void bindingTargetDataSource(RightRandomQueue<SwordData> rightRandomQueue) {
        this.rightRandomQueue = rightRandomQueue;
    }

    @Override
    public void start() {
        if(rightRandomQueue == null){
            logger.error("target data source rightRandomQueue is null");
            throw new NullPointerException("rightRandomQueue");
        }
        try {
            replicator.addEventListener(new EventListener() {
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if(event instanceof Command){
                        Command command = (Command) event;
                        SwordData swordData = new SwordData();
                        swordData.setId(idGenerator.nextId());
                        swordData.setValue(SwordCommandBuilder.buildSwordCommand(command));
                        swordData.setTimestamp(System.currentTimeMillis());
                        swordData.setCrc("1");
                        rightRandomQueue.push(swordData);
                    }
                }
            });
            replicator.open();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            replicator.close();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }
}
