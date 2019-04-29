package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskMonitor;
import com.zq.sword.array.piper.job.storage.JobRuntimeStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: Job系统
 * @author: zhouqi1
 * @create: 2019-04-25 16:30
 **/
public class JobSystem {

    /**
     * Job容器
     */
    private Map<String, Job> jobs;

    /**
     * 数据分片存储系统
     */
    private JobRuntimeStorage jobRuntimeStorage;

    /**
     * 任务监控器
     */
    private TaskMonitor taskMonitor;

    public JobSystem(JobRuntimeStorage jobRuntimeStorage, TaskMonitor taskMonitor) {
        this.jobRuntimeStorage = jobRuntimeStorage;
        this.taskMonitor = taskMonitor;
        jobs = new ConcurrentHashMap<>();
    }



    /**
     * 创建Job
     * @param jobEnv Job环境
     */
    public void createJob(JobEnv jobEnv){
        Job job = new Job(new JobContext(jobEnv, jobRuntimeStorage, taskMonitor));
        jobs.put(job.name(), job);
    }

    /**
     * 启动任务
     * @param jobName
     */
    public void startJob(String jobName){
        Job job = jobs.get(jobName);
        if(job == null){
           throw new NullPointerException("job is not exits");
        }
        job.start();
    }

    /**
     * 启动任务
     * @param jobName
     */
    public void destroyJob(String jobName){
        Job job = jobs.get(jobName);
        if(job == null){
            throw new NullPointerException("job is not exits");
        }
        job.destroy();
        jobs.remove(jobName);
    }


    /**
     * 通过job名称获取job
     * @param name
     * @return
     */
    public Job getJob(String name){
        return jobs.get(name);
    }
}
