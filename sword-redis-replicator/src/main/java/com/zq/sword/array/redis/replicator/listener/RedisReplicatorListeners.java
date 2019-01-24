package com.zq.sword.array.redis.replicator.listener;

import com.zq.sword.array.redis.command.RedisCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 监听器处理器
 * @author: zhouqi1
 * @create: 2019-01-24 16:51
 **/
public class RedisReplicatorListeners {

    private List<RedisReplicatorListener> listeners;

    public RedisReplicatorListeners() {
        this.listeners = new ArrayList<>();
    }

    public void addRedisReplicatorListener(RedisReplicatorListener listener){
        listeners.add(listener);
    }

    /**
     * 接收命令
     * @param command
     */
    public void receive(RedisCommand command){
        for(RedisReplicatorListener listener : listeners){
            listener.receive(command);
        }
    }

}
