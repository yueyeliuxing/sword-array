package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.msg.Message;
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

    private IdGenerator idGenerator;

    private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

    private SlaveRedisReplicator redisReplicator;

    private RedisWriter redisWriter;

    private RedisCommandDeserializer redisCommandDeserializer = new RedisCommandDeserializer();

    public RedisPiper(PiperConfig config) {
        super(config);

        idGenerator = new SnowFlakeIdGenerator();

        cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(config.redisUri());//new EmbeddedSlaveRedisReplicator(config.redisUri(), namePiper.getGroup(), this, cycleDisposeHandler);
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor());
        redisReplicator.addRedisReplicatorListener(new RedisCommandListener());
        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(config.redisConfig());
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor());
    }

    @Override
    protected void receiveMsg(Message message) {
        redisWriter.write(redisCommandDeserializer.deserialize(message.getBody()));
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
     * redis 命令监听器
     */
    private class RedisCommandListener implements RedisReplicatorListener {

        private RedisCommandSerializer redisCommandSerializer = new RedisCommandSerializer();

        @Override
        public void receive(RedisCommand command) {
            Message message = new Message();
            message.setMsgId(idGenerator.nextId());
            message.setTopic(namePiper.getGroup());
            message.setTag(command.getType()+"");
            message.setBody(redisCommandSerializer.serialize(command));
            message.setTimestamp(System.currentTimeMillis());
            sendMsg(message);
        }
    }

    /**
     * 循环命令过滤拦截器
     */
    private class CycleCommandFilterInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        @Override
        public RedisCommand interceptor(RedisCommand command) {
            if(cycleDisposeHandler.isCycleData(command)){
                return null;
            }
            return command;
        }
    }


    /**
     * 循环命令添加拦截器
     */
    private class CycleCommandAddInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        @Override
        public void onAcknowledgment(CommandMetadata metadata) {
            Exception exception = metadata.getException();
            if(exception == null){
                cycleDisposeHandler.addCycleData(metadata.getCommand());
            }
        }
    }


}
