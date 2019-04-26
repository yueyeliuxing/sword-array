package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.tasks.AbstractThreadActuator;

/**
 * @program: sword-array
 * @description: 抽象任务
 * @author: zhouqi1
 * @create: 2019-04-25 14:46
 **/
public abstract class AbstractTask extends AbstractThreadActuator implements Task {

    public static final int NEW = 0;
    public static final int START = 1;
    public static final int STOP = 2;
    public static final int EXCEPTION = 3;

    /**
     * 任务状态 0 未开启 1 已开启 2 发生异常
     */
    private int state = NEW;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务监控器
     */
    protected TaskMonitor taskMonitor;

    public AbstractTask(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int state() {
        return state;
    }

    /**
     * 设置任务状态
     * @param state
     */
    protected void state(int state) {
        this.state = state;
        taskMonitor.monitor(new TaskHealth(name, state));
    }

    /**
     * 设置任务监控器
     * @param taskMonitor
     */
    @Override
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }

    @Override
    public void handleEx(Throwable e) {
        state(EXCEPTION);
        taskMonitor.monitor(new TaskHealth(name, EXCEPTION, e.getMessage()));
    }

    @Override
    public void run() {
        state(START);
    }

    @Override
    public void stop() {
        state(STOP);
    }
}
