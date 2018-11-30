package com.zq.sword.array.redis.replicator;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
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

    private DataQueue<SwordData> dataQueue;

    private IdGenerator idGenerator;

    private SwordSlaveRedisReplicator(long workerId, long datacenterId, String uri, DataQueue<SwordData> dataQueue) {
        logger.info("SwordSlaveRedisReplicator start...");
        idGenerator = new SnowFlakeIdGenerator(workerId, datacenterId);
        this.dataQueue = dataQueue;
        try {
            replicator = new RedisReplicator(uri);
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    public static class SwordSlaveRedisReplicatorBuilder {
        private long workerId;
        private long datacenterId;
        private String uri;
        private DataQueue<SwordData> dataQueue;

        public static SwordSlaveRedisReplicatorBuilder create(){
            return new SwordSlaveRedisReplicatorBuilder();
        }

        public SwordSlaveRedisReplicatorBuilder idGenerat(long workerId, long datacenterId){
            this.workerId = workerId;
            this.datacenterId = datacenterId;
            return this;
        }

        public SwordSlaveRedisReplicatorBuilder listen(String uri){
            this.uri = uri;
            return this;
        }

        public SwordSlaveRedisReplicatorBuilder bindingDataSource(DataQueue<SwordData> dataQueue){
            this.dataQueue = dataQueue;
            return this;
        }

        public SwordSlaveRedisReplicator build(){
            return new SwordSlaveRedisReplicator(workerId, datacenterId, uri, dataQueue);
        }
    }

    @Override
    public void start() {
        new Thread(()->{
            if(dataQueue == null){
                logger.error("target data source dataQueue is null");
                throw new NullPointerException("dataQueue");
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
                            System.out.println(swordData);
                            dataQueue.push(swordData);
                        }
                    }
                });
                replicator.open();
            } catch (IOException e) {
                logger.error("error", e);
                throw new RuntimeException(e);
            }
        }).start();

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
