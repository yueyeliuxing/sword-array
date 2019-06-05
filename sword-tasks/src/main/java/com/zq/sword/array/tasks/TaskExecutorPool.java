package com.zq.sword.array.tasks;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: sword-array
 * @description: 任务执行器池
 * @author: zhouqi1
 * @create: 2019-06-05 10:02
 **/
public class TaskExecutorPool {

    /**
     * 单个线程引用
     */
    private static final Set<ThreadActuator> THREAD_ACTUATORS = new CopyOnWriteArraySet<>();

    /**
     * 任务执行器集合
     */
    private static final Set<TaskExecutor> TASK_EXECUTORS = new CopyOnWriteArraySet<>();

    /**
     * 任务线程池
     */
    private static final TaskExecutor COMMON_TASK_EXECUTOR = new SingleTaskExecutor(10);

    /**
     * 定时任务线程池
     */
    private static final TimedTaskExecutor COMMON_TIMED_TASK_EXECUTOR = new SingleTimedTaskExecutor(10);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            THREAD_ACTUATORS.forEach(threadActuator -> threadActuator.stop());
            TASK_EXECUTORS.forEach(taskExecutor -> taskExecutor.shutdown());
            COMMON_TASK_EXECUTOR.shutdown();
            COMMON_TIMED_TASK_EXECUTOR.shutdown();
        }));
    }

    /**
     * 创建线程执行器
     * @param runnable
     * @return
     */
    public static ThreadActuator buildThreadActuator(Runnable runnable){
        ThreadActuator threadActuator = new ThreadActuator(runnable);
        THREAD_ACTUATORS.add(threadActuator);
        return threadActuator;
    }

    /**
     * 释放 线程执行器
     * @param threadActuator
     */
    public static void releaseThreadActuator(ThreadActuator threadActuator){
        threadActuator.stop();
        THREAD_ACTUATORS.remove(threadActuator);
    }

    /**
     * 创建线程执行器
     * @param num
     * @return
     */
    public static TaskExecutor buildTaskExecutor(int num){
        TaskExecutor taskExecutor = new SingleTaskExecutor(num);
        TASK_EXECUTORS.add(taskExecutor);
        return taskExecutor;
    }

    /**
     * 释放 线程执行器
     * @param taskExecutor
     */
    public static void releaseTaskExecutor(TaskExecutor taskExecutor){
        TASK_EXECUTORS.remove(taskExecutor);
    }

    /**
     * 得到任务执行器
     * @return
     */
    public static TaskExecutor getCommonTaskExecutor(){
        return COMMON_TASK_EXECUTOR;
    }
    /**
     * 得到定时任务线程池
     * @return
     */
    public static TimedTaskExecutor getCommonTimedTaskExecutor(){
        return COMMON_TIMED_TASK_EXECUTOR;
    }
}
