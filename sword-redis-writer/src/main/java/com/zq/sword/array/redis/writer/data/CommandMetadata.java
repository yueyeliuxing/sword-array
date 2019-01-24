package com.zq.sword.array.redis.writer.data;

import com.zq.sword.array.redis.command.RedisCommand;
import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 命令元信息
 * @author: zhouqi1
 * @create: 2019-01-24 14:53
 **/
@Data
@ToString
public class CommandMetadata {

    /**
     * 当前的命令
     */
    private RedisCommand command;

    /**
     * 异常
     */
    private Exception exception;

    public CommandMetadata(RedisCommand command) {
        this.command = command;
    }

    public CommandMetadata(RedisCommand command, Exception exception) {
        this.command = command;
        this.exception = exception;
    }
}
