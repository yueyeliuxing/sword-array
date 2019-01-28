package com.zq.sword.array.redis.handler;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据循环处理
 * @author: zhouqi1
 * @create: 2018-10-22 16:27
 **/
public class SimpleCycleDisposeHandler implements CycleDisposeHandler<RedisCommand> {

    private Logger logger = LoggerFactory.getLogger(SimpleCycleDisposeHandler.class);

    private Set<RedisCommand> consumedSwordDataSet;

    private DelayQueue<DelayedCommand> commandDelayQueue;

    private TimedTaskExecutor taskExecutor;

    public SimpleCycleDisposeHandler() {
        logger.info("SwordCommandCycleDisposeBridge 模块启动成功");
        consumedSwordDataSet = new CopyOnWriteArraySet<>();
        commandDelayQueue = new DelayQueue<>();
        taskExecutor = new SingleTimedTaskExecutor();
        startTasks();
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //只保存两分钟的数据
        taskExecutor.timedExecute(()->{
            DelayedCommand delayedCommand = commandDelayQueue.poll();
            consumedSwordDataSet.remove(delayedCommand.command);
        }, 2, TimeUnit.MINUTES);
    }


    @Override
    public boolean isCycleData(RedisCommand command) {
        return consumedSwordDataSet.contains(command);
    }

    @Override
    public boolean addCycleData(RedisCommand command) {
        commandDelayQueue.add(new DelayedCommand(command, System.currentTimeMillis() + 2000));
        return consumedSwordDataSet.add(command);
    }


    private class DelayedCommand implements Delayed {

        private RedisCommand command;

        private long expires;

        public DelayedCommand(RedisCommand command, long expires) {
            this.command = command;
            this.expires = expires;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return expires - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            if(o instanceof DelayedCommand){
                DelayedCommand delayedCommand = (DelayedCommand)o;
                return this.equals(delayedCommand) ? 0 : (int)(this.expires - delayedCommand.expires);
            }
            return 0;
        }
    }
}
