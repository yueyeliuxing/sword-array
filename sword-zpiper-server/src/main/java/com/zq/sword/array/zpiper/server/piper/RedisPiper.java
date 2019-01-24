package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.mq.jade.consumer.ConsumeStatus;
import com.zq.sword.array.mq.jade.consumer.MessageListener;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.handler.SimpleCycleDisposeHandler;
import com.zq.sword.array.redis.replicator.EmbeddedSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.writer.*;
import com.zq.sword.array.redis.writer.data.CommandMetadata;
import com.zq.sword.array.redis.writer.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.writer.interceptor.CommandInterceptor;
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

    private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

    private SlaveRedisReplicator redisReplicator;

    private RedisWriter redisWriter;

    public RedisPiper(PiperConfig config) {
        super(config);

        cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //设置redis 复制器
        redisReplicator = new EmbeddedSlaveRedisReplicator(config.redisUri(), namePiper.getGroup(), this, cycleDisposeHandler);

        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(config.redisConfig());
        redisWriter.addInterceptor(new CycleCommandInterceptor());
    }

    @Override
    protected MessageListener createMessageListener() {
        return new MessageListener() {

            private RedisCommandDeserializer redisCommandDeserializer = new RedisCommandDeserializer();

            @Override
            public ConsumeStatus consume(Message message) {
                redisWriter.write(redisCommandDeserializer.deserialize(message.getBody()));
                return ConsumeStatus.CONSUME_SUCCESS;
            }
        };
    }

    @Override
    protected void doStartModule() {
        redisWriter.start();
        redisReplicator.start();
    }

    @Override
    protected void doStopModule() {
        redisReplicator.stop();
        redisWriter.stop();
    }

    /**
     * 循环命令拦截器
     */
    private class CycleCommandInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        @Override
        public void onAcknowledgment(CommandMetadata metadata) {
            Exception exception = metadata.getException();
            if(exception == null){
                cycleDisposeHandler.addCycleData(metadata.getCommand());
            }
        }
    }


}
