package com.zq.sword.array.redis.slave;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.common.data.SwordCommandSerializer;
import com.zq.sword.array.common.data.SwordData;
import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
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

    public SwordSlaveRedisReplicator(String uri, RightRandomQueue<SwordData> rightRandomQueue) {
        logger.info("SwordSlaveRedisReplicator start...");
        this.rightRandomQueue = rightRandomQueue;
        idGenerator = new SnowFlakeIdGenerator(0, 0);
        try {
            replicator = new RedisReplicator(uri);
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
                        SwordSlaveRedisReplicator.this.rightRandomQueue.push(swordData);
                    }
                }
            });
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try {
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

    @Override
    public void reset() {
        try {
            replicator.close();
            replicator.open();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }
}
