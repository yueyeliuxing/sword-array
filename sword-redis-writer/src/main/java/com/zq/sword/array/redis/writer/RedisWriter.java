package com.zq.sword.array.redis.writer;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.callback.CommandCallback;
import com.zq.sword.array.redis.writer.interceptor.CommandInterceptor;
import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: rdis 命令写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:42
 **/
public interface RedisWriter extends Actuator {

    /**
     * 添加拦截器
     * @param interceptor
     */
    void addInterceptor(CommandInterceptor interceptor);

    /**
     * 写入
     * @param command
     */
    boolean write(RedisCommand command);

    /**
     * 写入
     * @param command
     * @param callback
     */
    void write(RedisCommand command, CommandCallback callback);

}
