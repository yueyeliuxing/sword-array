package com.zq.sword.array.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 服务接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:25
 **/
public class SingleTimedTaskExecutor implements TimedTaskExecutor {

    private ScheduledExecutorService timedExecutor;

    public SingleTimedTaskExecutor() {
        timedExecutor = Executors.newScheduledThreadPool(1);
    }

    public SingleTimedTaskExecutor(int num) {
        timedExecutor = Executors.newScheduledThreadPool(num);
    }

    @Override
    public void timedExecute(Task task, long delay, TimeUnit timeUnit) {
        timedExecutor.scheduleWithFixedDelay(()->{task.execute();}, 0, delay, timeUnit);
    }

    @Override
    public void shutdown() {
        timedExecutor.shutdown();
    }
}
