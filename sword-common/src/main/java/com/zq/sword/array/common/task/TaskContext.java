package com.zq.sword.array.common.task;

/**
 * @program: sword-array
 * @description: 任务上下文
 * @author: zhouqi1
 * @create: 2018-07-31 20:44
 **/
public interface TaskContext extends Runnable {

    /**
     * 添加任务
     * @param task
     */
    void addTask(Task task);
}
