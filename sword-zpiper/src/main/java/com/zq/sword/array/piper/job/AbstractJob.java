package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;
import com.zq.sword.array.tasks.AbstractThreadActuator;

import static com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth.*;

/**
 * @program: sword-array
 * @description: 抽象任务
 * @author: zhouqi1
 * @create: 2019-04-25 14:46
 **/
public abstract class AbstractJob extends AbstractThreadActuator implements Job {

    /**
     * 任务状态 1 创建完成 2 启动成功 3 停止 4发生异常
     */
    protected int state = NEW;

    /**
     * 任务名称
     */
    protected String name;

    /**
     * 任务监控器
     */
    protected JobMonitor jobMonitor;

    public AbstractJob(String name, JobMonitor jobMonitor) {
        this.name = name;
        this.jobMonitor = jobMonitor;
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
        jobMonitor.monitor(new JobHealth(name, state));
    }

    @Override
    public void handleEx(Throwable e) {
        state(EXCEPTION);
        jobMonitor.monitor(new JobHealth(name, EXCEPTION, e.getMessage()));
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
