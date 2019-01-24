package com.zq.sword.array.redis.writer;

import com.zq.sword.array.redis.writer.client.RedisClient;
import com.zq.sword.array.redis.writer.data.AsyRedisCommand;
import com.zq.sword.array.redis.writer.data.CommandMetadata;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 命令发送器
 * @author: zhouqi1
 * @create: 2019-01-24 15:42
 **/
public class CommandSender extends AbstractThreadActuator {

    private Logger logger = LoggerFactory.getLogger(CommandSender.class);

   private CommandAccumulator accumulator;

   private RedisClient redisClient;

    public CommandSender(CommandAccumulator accumulator, RedisClient redisClient) {
        this.accumulator = accumulator;
        this.redisClient = redisClient;
    }

    @Override
    public void run() {
        while (!isClose && !Thread.currentThread().isInterrupted()){
            AsyRedisCommand command = accumulator.poll();
            if(command == null){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isClose = true;
                   logger.error("线程发生中断异常",e);
                }
            }else {
                try {
                    redisClient.write(command.getCommand());
                    command.getCallback().callback(new CommandMetadata(command.getCommand()));
                } catch (Exception e) {
                    command.getCallback().callback(new CommandMetadata(command.getCommand(), e));
                    logger.error("redis 命令写入异常",e);
                }
            }
        }
    }
}
