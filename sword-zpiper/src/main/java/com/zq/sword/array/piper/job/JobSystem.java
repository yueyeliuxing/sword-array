package com.zq.sword.array.piper.job;

import com.zq.sword.array.piper.storage.RedisDataStorage;

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
    private RedisDataStorage redisDataStorage;

    /**
     * 任务监控器
     */
    private JobMonitor jobMonitor;

    public JobSystem(RedisDataStorage redisDataStorage) {
        this.redisDataStorage = redisDataStorage;
        jobs = new ConcurrentHashMap<>();
    }

    public void setJobMonitor(JobMonitor jobMonitor) {
        this.jobMonitor = jobMonitor;
    }

    /**
     * 创建Job
     * @param jobContext Job环境
     */
    public void createJob(JobContext jobContext){
        Job job = new RedisJob(jobContext, redisDataStorage, jobMonitor);
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
