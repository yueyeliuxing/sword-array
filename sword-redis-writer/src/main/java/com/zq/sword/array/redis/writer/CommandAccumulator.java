package com.zq.sword.array.redis.writer;

import com.zq.sword.array.redis.writer.callback.AsyRedisCommand;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: sword-array
 * @description: 命令存储器
 * @author: zhouqi1
 * @create: 2019-01-24 15:31
 **/
public class CommandAccumulator {

    private BlockingQueue<AsyRedisCommand> commandQueue;

    public CommandAccumulator() {
        this.commandQueue = new LinkedBlockingQueue<>();
    }

    public void add(AsyRedisCommand command){
        commandQueue.add(command);
    }

    public AsyRedisCommand poll(){
        return commandQueue.poll();
    }
}
