package com.zq.sword.array.redis.replicator.listener;

import com.zq.sword.array.redis.command.RedisCommand;

/**
 * @program: sword-array
 * @description: redis 复制监听器
 * @author: zhouqi1
 * @create: 2019-01-24 16:47
 **/
public interface RedisReplicatorListener {

    /**
     * 接收命令
     * @param command
     */
    void receive(RedisCommand command);
}
