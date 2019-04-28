package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskMonitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: Job系统
 * @author: zhouqi1
 * @create: 2019-04-25 16:30
 **/
public class JobSystem {

    private Map<String, Job> jobs;

    private TaskMonitor taskMonitor;

    private JobSystem() {
        jobs = new ConcurrentHashMap<>();
    }

    private static class JobSystemBuilder {
        public static JobSystem JOB_SYSTEM = new JobSystem();
    }

    /**
     * 获取单例对象
     * @return
     */
    public static JobSystem getInstance(){
        return JobSystemBuilder.JOB_SYSTEM;
    }

    /**
     * 创建Job
     * @param context 任务上下文
     * @param monitor 任务监听器
     */
    public void createJob(JobContext context, TaskMonitor monitor){
        Job job = new Job(context);
        job.setTaskMonitor(monitor);
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
