package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.structure.queue.FileResourceQueue;
import com.zq.sword.array.data.structure.queue.ResourceQueue;
import com.zq.sword.array.mq.jade.consumer.ConsumeStatus;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.consumer.MessageListener;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:43
 **/
public abstract class AbstractRedisWriter extends AbstractThreadActuator implements RedisWriter {

    private Logger logger = LoggerFactory.getLogger(AbstractRedisWriter.class);

    private ResourceQueue<RedisCommand> resourceQueue;

    private RedisClient<RedisCommand> redisClient;

    private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

    private Consumer consumer;

    public AbstractRedisWriter(RedisConfig redisConfig, String fileLocation, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        redisClient = new SwordRedisClient(redisConfig);
        this.resourceQueue = new FileResourceQueue(fileLocation, new RedisCommandSerializer(), new RedisCommandDeserializer());
        this.cycleDisposeHandler = cycleDisposeHandler;
        this.consumer = createConsumer();
        this.consumer.bindingMessageListener(new RedisCommandMessageListener());
    }

    protected abstract Consumer createConsumer();

    @Override
    public void run() {
        consumer.start();
        while (!isClose && !Thread.currentThread().isInterrupted()){
            RedisCommand redisCommand = resourceQueue.poll();
            if(redisCommand != null){
                logger.error("get dataQueue data {}", redisCommand);
                redisClient.write(redisCommand);
                cycleDisposeHandler.addCycleData(redisCommand);
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
