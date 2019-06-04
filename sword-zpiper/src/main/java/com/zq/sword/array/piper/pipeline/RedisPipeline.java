package com.zq.sword.array.piper.pipeline;

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
import com.zq.sword.array.redis.util.RedisConfig;
import com.zq.sword.array.redis.writer.DefaultRedisWriter;
import com.zq.sword.array.redis.writer.RedisWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: Redis 管道
 * @author: zhouqi1
 * @create: 2019-06-03 10:19
 **/
public class RedisPipeline implements Pipeline<byte[]> {

    private Logger logger  = LoggerFactory.getLogger(RedisPipeline.class);

    /**
     * Redis 复制器
     */
    private SlaveRedisReplicator redisReplicator;

    /**
     * Redis 写入器
     */
    private RedisWriter redisWriter;

    public RedisPipeline(String redisUri) {

        CycleDisposeHandler<RedisCommand> cycleDisposeHandler = new SimpleCycleDisposeHandler();

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(redisUri);
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor(cycleDisposeHandler));

        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(new RedisConfig(redisUri));
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor(cycleDisposeHandler));
    }

    @Override
    public void outflow(OutflowListener listener) {
        redisReplicator.addRedisReplicatorListener(new RedisReplicatorListener(){
            private RedisCommandSerializer redisCommandSerializer = new RedisCommandSerializer();
            @Override
            public void receive(RedisCommand command) {
                listener.outflow(redisCommandSerializer.serialize(command));
            }
        });
    }

    @Override
    public void open() {
        redisReplicator.start();
        redisWriter.start();
    }

    @Override
    public void inflow(byte[] data) {
        redisWriter.write(new RedisCommandDeserializer().deserialize(data), metadata -> {
            if(metadata.getException() != null){
                logger.error("写入redis出错", metadata.getException());
            }
        });
    }

    @Override
    public void close() {
        redisReplicator.stop();
        redisWriter.stop();
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
