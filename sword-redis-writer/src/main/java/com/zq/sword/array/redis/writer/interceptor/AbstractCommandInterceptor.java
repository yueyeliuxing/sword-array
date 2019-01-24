package com.zq.sword.array.redis.writer.interceptor;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.data.CommandMetadata;

/**
 * @program: sword-array
 * @description: 抽象 命令拦截器
 * @author: zhouqi1
 * @create: 2019-01-24 16:34
 **/
public abstract class AbstractCommandInterceptor implements CommandInterceptor {

    @Override
    public RedisCommand onWrite(RedisCommand command) {
        return null;
    }

    @Override
    public void onAcknowledgment(CommandMetadata metadata) {

    }
}
