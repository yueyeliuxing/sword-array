package com.zq.sword.array.redis.writer.data;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.callback.CommandCallback;
import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 异步命令
 * @author: zhouqi1
 * @create: 2019-01-24 15:35
 **/
@Data
@ToString
public class AsyRedisCommand {

    private RedisCommand command;

    private CommandCallback callback;

    public AsyRedisCommand(RedisCommand command, CommandCallback callback) {
        this.command = command;
        this.callback = callback;
    }
}
