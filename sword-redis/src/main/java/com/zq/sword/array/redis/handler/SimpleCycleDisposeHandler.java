package com.zq.sword.array.redis.handler;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
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

    private TimedTaskExecutor taskExecutor;

    public SimpleCycleDisposeHandler() {
        logger.info("SwordCommandCycleDisposeBridge 模块启动成功");
        consumedSwordDataSet = new CopyOnWriteArraySet<>();
        taskExecutor = new SingleTimedTaskExecutor();
    }

    public void start(){
        startTasks();
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //只保存两分钟的数据
        taskExecutor.timedExecute(()->{
            consumedSwordDataSet.clear();
        }, 2, TimeUnit.MINUTES);
    }


    @Override
    public boolean isCycleData(RedisCommand command) {
        return consumedSwordDataSet.contains(command);
    }

    @Override
    public boolean addCycleData(RedisCommand command) {
        return consumedSwordDataSet.add(command);
    }
}
