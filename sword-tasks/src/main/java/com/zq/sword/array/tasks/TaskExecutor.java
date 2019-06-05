package com.zq.sword.array.tasks;

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
     * 关闭
     */
    void shutdown();
}
