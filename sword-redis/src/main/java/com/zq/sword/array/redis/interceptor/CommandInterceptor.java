package com.zq.sword.array.redis.interceptor;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.CommandMetadata;

/**
 * @program: sword-array
 * @description: redis命令拦截器
 * @author: zhouqi1
 * @create: 2019-01-24 14:47
 **/
public interface CommandInterceptor {

    /**
     * 命令写入之前进行拦截处理
     * @param command
     * @return 如果返回 null 命令被抛弃
     */
    RedisCommand interceptor(RedisCommand command);

    /**
     * 写入后的应答拦截
     * @param metadata
     */
    void onAcknowledgment(CommandMetadata metadata);
}
