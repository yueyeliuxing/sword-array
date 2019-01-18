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

    private ExecutorService executor;

    private ScheduledExecutorService tomedExecutor;

    public SingleTimedTaskExecutor() {
        executor = Executors.newFixedThreadPool(1);
        tomedExecutor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void execute(Task task) {
        executor.submit(()->{task.execute();});
    }

    @Override
    public void timedExecute(Task task, long delay, TimeUnit timeUnit) {
        tomedExecutor.scheduleWithFixedDelay(()->{task.execute();}, 0, delay, timeUnit);
    }
}
