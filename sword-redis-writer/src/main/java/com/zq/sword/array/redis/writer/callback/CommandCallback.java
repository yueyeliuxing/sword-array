package com.zq.sword.array.redis.writer.callback;

import com.zq.sword.array.redis.command.CommandMetadata;

/**
 * @program: sword-array
 * @description: redis命令异步写入回调
 * @author: zhouqi1
 * @create: 2019-01-24 15:23
 **/
public interface CommandCallback {

    /**
     * 命令写入回调
     * @param metadata
     */
    void callback(CommandMetadata metadata);
}
