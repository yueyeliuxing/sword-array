package com.zq.sword.array.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: sword-array
 * @description: 服务接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:25
 **/
public class SingleTaskExecutor implements TaskExecutor {

    private ExecutorService executor;

    public SingleTaskExecutor() {
        executor = Executors.newFixedThreadPool(1);
    }

    public SingleTaskExecutor(int num) {
        executor = Executors.newFixedThreadPool(num);
    }

    @Override
    public void execute(Task task) {
        executor.submit(()->{task.execute();});
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
