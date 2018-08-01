package com.zq.sword.array.common.service;

import com.zq.sword.array.common.task.Task;

import java.util.ArrayList;
import java.util.List;
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
public abstract class AbstractTaskService extends AbstractService implements TaskService {

    private ExecutorService executor;

    private ScheduledExecutorService tomedExecutor;

    public AbstractTaskService() {
        executor = Executors.newFixedThreadPool(1);
        tomedExecutor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void loadTask(Task task) {
        executor.submit(()->{task.execute();});
    }

    @Override
    public void loadTimedTask(Task task, long delay, TimeUnit timeUnit) {
        tomedExecutor.scheduleWithFixedDelay(()->{task.execute();}, 0, delay, timeUnit);
    }
}
