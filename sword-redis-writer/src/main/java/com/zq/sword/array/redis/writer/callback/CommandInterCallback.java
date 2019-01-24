package com.zq.sword.array.redis.writer.callback;

import com.zq.sword.array.redis.writer.data.CommandMetadata;
import com.zq.sword.array.redis.writer.interceptor.CommandInterceptors;

/**
 * @program: sword-array
 * @description: 拦截器callback
 * @author: zhouqi1
 * @create: 2019-01-24 16:00
 **/
public class CommandInterCallback implements CommandCallback {

    private CommandCallback callback;

    private CommandInterceptors interceptors;

    public CommandInterCallback(CommandCallback callback, CommandInterceptors interceptors) {
        this.callback = callback;
        this.interceptors = interceptors;
    }

    @Override
    public void callback(CommandMetadata metadata) {
        interceptors.onAcknowledgment(metadata);
        callback.callback(metadata);
    }
}
