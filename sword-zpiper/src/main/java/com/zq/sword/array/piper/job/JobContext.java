package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.monitor.TaskMonitor;
import com.zq.sword.array.piper.job.storage.JobRuntimeStorage;
import lombok.Data;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务上下文
 * @author: zhouqi1
 * @create: 2019-04-28 10:30
 **/
@Data
public class JobContext {

    /**
     * Job环境
     */
    private JobEnv jobEnv;

    /**
     * Job运行时存储
     */
    private JobRuntimeStorage jobRuntimeStorage;

    /**
     * 任务监控器
     */
    private TaskMonitor taskMonitor;

    public JobContext(JobEnv jobEnv, JobRuntimeStorage jobRuntimeStorage, TaskMonitor taskMonitor) {
        this.jobEnv = jobEnv;
        this.jobRuntimeStorage = jobRuntimeStorage;
        this.taskMonitor = taskMonitor;
    }

    public String getName() {
        return jobEnv.getName();
    }

    public String getPiperGroup() {
        return jobEnv.getPiperGroup();
    }

    public String getSourceRedis() {
        return jobEnv.getSourceRedis();
    }

    public List<String> getBackupPipers() {
        return jobEnv.getBackupPipers();
    }

    public List<String> getConsumePipers() {
        return jobEnv.getConsumePipers();
    }

    public JobRuntimeStorage getJobRuntimeStorage() {
        return jobRuntimeStorage;
    }

    public void setJobRuntimeStorage(JobRuntimeStorage jobRuntimeStorage) {
        this.jobRuntimeStorage = jobRuntimeStorage;
    }

    public TaskMonitor getTaskMonitor() {
        return taskMonitor;
    }
}
