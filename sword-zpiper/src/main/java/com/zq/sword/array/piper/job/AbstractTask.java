package com.zq.sword.array.piper.job;

import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskHealth;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskMonitor;
import com.zq.sword.array.piper.job.storage.JobRuntimeStorage;

import static com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskHealth.*;

/**
 * @program: sword-array
 * @description: 抽象任务
 * @author: zhouqi1
 * @create: 2019-04-25 14:46
 **/
public abstract class AbstractTask extends AbstractThreadActuator implements Task {

    /**
     * 任务状态 1 创建完成 2 启动成功 3 停止 4发生异常
     */
    protected int state = NEW;

    /**
     * 所属的Job
     */
    protected Job job;

    /**
     * 任务名称
     */
    protected String name;

    /**
     * job运行时数据存储
     */
    protected JobRuntimeStorage jobRuntimeStorage;

    /**
     * 任务监控器
     */
    protected TaskMonitor taskMonitor;

    public AbstractTask(Job job, String name, JobRuntimeStorage jobRuntimeStorage, TaskMonitor taskMonitor) {
        this.job = job;
        this.name = name;
        this.jobRuntimeStorage = jobRuntimeStorage;
        this.taskMonitor = taskMonitor;
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
        taskMonitor.monitor(new TaskHealth(job.name(), name, state));
    }

    @Override
    public void handleEx(Throwable e) {
        state(EXCEPTION);
        taskMonitor.monitor(new TaskHealth(job.name(), name, EXCEPTION, e.getMessage()));
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
