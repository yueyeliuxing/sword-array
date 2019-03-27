package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.consumer.ConsumeStatus;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.consumer.MessageListener;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.redis.command.CommandMetadata;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.handler.SimpleCycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.replicator.DefaultSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.redis.writer.DefaultRedisWriter;
import com.zq.sword.array.redis.writer.RedisWriter;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisPiper extends AbstractPiper implements Piper{

    private Logger logger = LoggerFactory.getLogger(RedisPiper.class);

    protected Consumer consumer;

    protected Producer producer;

    private IdGenerator idGenerator;

    private SlaveRedisReplicator redisReplicator;

    private RedisWriter redisWriter;

    public RedisPiper(PiperConfig config) {
        super(config);

        //创建生产者
        this.producer = createProducer();

        //创建消费者
        this.consumer  = createConsumer(id()+"");
        this.consumer.bindingMessageListener(new ReceiveMessageListener());

        idGenerator = new SnowFlakeIdGenerator();

        CycleDisposeHandler<RedisCommand> cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(config.redisUri());
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor(cycleDisposeHandler));
        redisReplicator.addRedisReplicatorListener(new RedisCommandListener());
        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(config.redisConfig());
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor(cycleDisposeHandler));
    }

    /**
     * 接收消息监听器
     */
    public class ReceiveMessageListener implements MessageListener {

        @Override
        public ConsumeStatus consume(Message message) {
            try{
                logger.info("接收消息->{}", message);
                redisWriter.write(new RedisCommandDeserializer().deserialize(message.getBody()), metadata -> {
                    if(metadata.getException() != null){
                        logger.error("写入redis出错", metadata.getException());
                    }
                });
            }catch (Exception e){
                logger.error("接收消息发生异常", e);
                return ConsumeStatus.CONSUME_FAIL;
            }
            return ConsumeStatus.CONSUME_SUCCESS;
        }
    }

    @Override
    protected void doStartModule() {
        producer.start();
        consumer.start();
        redisWriter.start();
        redisReplicator.start();
    }

    @Override
    protected void doStopModule() {
        producer.stop();
        consumer.stop();
        redisReplicator.stop();
        redisWriter.stop();
    }

    /**
     * redis 命令监听器
     */
    private class RedisCommandListener implements RedisReplicatorListener {

        private RedisCommandSerializer redisCommandSerializer = new RedisCommandSerializer();

        @Override
        public void receive(RedisCommand command) {
            logger.info("接收到命令，时间：{}", System.currentTimeMillis());
            Message message = new Message();
            message.setMsgId(idGenerator.nextId());
            message.setTopic(namePiper.getGroup());
            message.setTag(command.getType()+"");
            message.setBody(redisCommandSerializer.serialize(command));
            message.setTimestamp(System.currentTimeMillis());
            producer.sendMsg(message);
        }
    }

    /**
     * 循环命令过滤拦截器
     */
    private class CycleCommandFilterInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

        public CycleCommandFilterInterceptor(CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
            this.cycleDisposeHandler = cycleDisposeHandler;
        }

        @Override
        public RedisCommand interceptor(RedisCommand command) {
            logger.info("命令比对->{}", command);
            if(cycleDisposeHandler.isCycleData(command)){
                logger.info("命令存在循环缓存->{}", command);
                return null;
            }
            return command;
        }
    }


    /**
     * 循环命令添加拦截器
     */
    private class CycleCommandAddInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

        public CycleCommandAddInterceptor(CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
            this.cycleDisposeHandler = cycleDisposeHandler;
        }

        @Override
        public void onAcknowledgment(CommandMetadata metadata) {
            Exception exception = metadata.getException();
            if(exception == null){
                logger.info("命令写入循环缓存->{}", metadata.getCommand());
                cycleDisposeHandler.addCycleData(metadata.getCommand());
            }
        }
    }


}
