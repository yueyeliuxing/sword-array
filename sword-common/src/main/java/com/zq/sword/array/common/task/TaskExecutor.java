package com.zq.sword.array.common.task;

import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 入去执行器
 * @author: zhouqi1
 * @create: 2018-10-17 16:18
 **/
public interface TaskExecutor {

    /**
     * 执行任务
     * @param task
     */
    void execute(Task task);

    /**
     * 定时执行任务
     * @param task
     * @param delay
     * @param timeUnit
     */
    void timedExecute(Task task, long delay, TimeUnit timeUnit);
}
