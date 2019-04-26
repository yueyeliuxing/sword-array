package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.handler.SimpleCycleDisposeHandler;

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
     * @param config
     */
    public void createJob(JobConfig config, TaskMonitor monitor){
        CycleDisposeHandler<RedisCommand> cycleDisposeHandler = new SimpleCycleDisposeHandler();
        Job job = new Job(config.getJobEnv().getName());
        job.setReplicateTask(new RedisReplicateTask(config.getJobEnv(), cycleDisposeHandler, config.getPartitionSystem()));
        job.setWriteTask(new RedisWriteTask(config.getJobEnv(), config.getPartitionSystem(), cycleDisposeHandler));
        job.setTaskMonitor(monitor);;
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
        job.stop();
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
