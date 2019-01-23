package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.structure.queue.FileResourceQueue;
import com.zq.sword.array.data.structure.queue.ResourceQueue;
import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.consumer.*;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.embedded.EmbeddedConsumeDispatcher;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:43
 **/
public class EmbeddedRedisWriter extends AbstractThreadActuator implements RedisWriter {

    private Logger logger = LoggerFactory.getLogger(EmbeddedRedisWriter.class);

    private ResourceQueue<RedisCommand> resourceQueue;

    private RedisClient<RedisCommand> redisClient;

    /**
     * 消费调度器
     */
    private ConsumeDispatcher consumeDispatcher;

    private EmbeddedRedisWriter(RedisConfig redisConfig, String fileLocation, Broker broker, NameCoordinator coordinator) {
        redisClient = new SwordRedisClient(redisConfig);
        this.resourceQueue = new FileResourceQueue(fileLocation, new RedisCommandSerializer(), new RedisCommandDeserializer());
        this.consumeDispatcher = new EmbeddedConsumeDispatcher(broker, coordinator);
    }

    @Override
    public void start() {
        super.start();
        consumeDispatcher.start();
        Consumer consumer = consumeDispatcher.createConsumer();
        consumer.bindingMessageListener(new RedisCommandMessageListener());
        consumer.start();
    }

    @Override
    public void run() {
        while (!isClose && !Thread.currentThread().isInterrupted()){
            RedisCommand redisCommand = resourceQueue.poll();
            if(redisCommand != null){
                logger.error("get dataQueue data {}", redisCommand);
                redisClient.write(redisCommand);
            }else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("current thread interrupt");
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        redisClient.close();
    }

    /**
     * redis 消息监听器
     */
    private class RedisCommandMessageListener implements MessageListener {

        private RedisCommandDeserializer redisCommandDeserializer;

        public RedisCommandMessageListener() {
            redisCommandDeserializer = new RedisCommandDeserializer();
        }

        @Override
        public ConsumeStatus consume(Message message) {
            resourceQueue.offer(redisCommandDeserializer.deserialize(message.getBody()));
            return ConsumeStatus.CONSUME_SUCCESS;
        }
    }

}
