package com.zq.sword.array.redis.replicator;


import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.producer.ProduceDispatcher;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.tasks.AbstractThreadActuator;
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
public class AbstractSlaveRedisReplicator extends AbstractThreadActuator implements SlaveRedisReplicator {

    private Logger logger = LoggerFactory.getLogger(AbstractSlaveRedisReplicator.class);

    private Replicator replicator;

    private ProduceDispatcher produceDispatcher;

    private IdGenerator idGenerator;

    private RedisCommandSerializer redisCommandSerializer;

    private String topic;

    public AbstractSlaveRedisReplicator(String uri, String topic, ProduceDispatcher produceDispatcher) {
        try {
            replicator = new RedisReplicator(uri);
            replicator.addEventListener(new RedisReplicatorEventListener());
        } catch (URISyntaxException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
        this.redisCommandSerializer = new RedisCommandSerializer();
        this.produceDispatcher = produceDispatcher;
        this.idGenerator = new SnowFlakeIdGenerator();
    }

    @Override
    public void run() {
        try {
            replicator.open();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        try {
            replicator.close();
        } catch (IOException e) {
            logger.error("error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * redis 复制时间
     */
    private class RedisReplicatorEventListener implements EventListener {

        @Override
        public void onEvent(Replicator replicator, Event event) {
            if(event instanceof Command){
                Command command = (Command) event;
                RedisCommand redisCommand = RedisCommandBuilder.buildSwordCommand(command);
                Message message = new Message();
                message.setMsgId(idGenerator.nextId());
                message.setTopic(topic);
                message.setTag(redisCommand.getType()+"");
                message.setBody(redisCommandSerializer.serialize(redisCommand));
                message.setTimestamp(System.currentTimeMillis());
                Producer producer = produceDispatcher.createProducer();
                producer.start();
                producer.sendMsg(message);
                producer.stop();
            }
        }
    }
}
