package com.zq.sword.array.common.service;

import com.zq.sword.array.common.task.Task;
import java.util.concurrent.TimeUnit;

/**
 * 任务服务
 */
public interface TaskService extends Service {


    /**
     * 加载任务
     * @param task
     */
    void loadTask(Task task);

    /**
     * 加载定时任务任务
     * @param task
     */
    void loadTimedTask(Task task, long delay, TimeUnit timeUnit);

}
