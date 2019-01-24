package com.zq.sword.array.redis.writer;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.callback.CommandCallback;
import com.zq.sword.array.redis.writer.callback.CommandInterCallback;
import com.zq.sword.array.redis.writer.client.RedisClient;
import com.zq.sword.array.redis.writer.callback.AsyRedisCommand;
import com.zq.sword.array.redis.command.CommandMetadata;
import com.zq.sword.array.redis.util.RedisConfig;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:43
 **/
public class DefaultRedisWriter implements RedisWriter {

    private Logger logger = LoggerFactory.getLogger(DefaultRedisWriter.class);

    private CommandInterceptors interceptors;

    private CommandAccumulator accumulator;

    private RedisClient redisClient;

    private CommandSender sender;

    public DefaultRedisWriter(RedisConfig redisConfig) {
        this.redisClient = new RedisClient(redisConfig);
        this.interceptors = new CommandInterceptors();
        this.accumulator = new CommandAccumulator();
        this.sender = new CommandSender(this.accumulator, this.redisClient);
    }

    @Override
    public void addCommandInterceptor(CommandInterceptor interceptor) {
        interceptors.addCommandInterceptor(interceptor);
    }

    @Override
    public void start() {
        this.sender.start();
    }

    @Override
    public void stop() {
        sender.stop();
        redisClient.close();

    }

    @Override
    public boolean write(RedisCommand command) {
        //拦截器处理
        if((command = interceptors.interceptor(command)) == null){
            return false;
        }
        try {
            redisClient.write(command);
            interceptors.onAcknowledgment(new CommandMetadata(command));
        } catch (Exception e) {
            logger.error("write command:{} error：{}", command, e);
            interceptors.onAcknowledgment(new CommandMetadata(command, e));
            return false;
        }
        return true;
    }

    @Override
    public void write(RedisCommand command, CommandCallback callback) {
        //拦截器处理
        if((command = interceptors.interceptor(command)) == null){
            return;
        }
        accumulator.add(new AsyRedisCommand(command, new CommandInterCallback(callback, interceptors)));
    }

}
