package com.zq.sword.array.namer.service;

import com.zq.sword.array.namer.job.MetaJobSupervisor;
import com.zq.sword.array.rpc.api.namer.JobHandleService;
import com.zq.sword.array.rpc.api.namer.dto.JobHealth;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;

/**
 * @program: sword-array
 * @description: Job处理器
 * @author: zhouqi1
 * @create: 2019-06-14 15:50
 **/
public class MetaJobHandleService implements JobHandleService {
    /**
     * Job管理器
     */
    private MetaJobSupervisor metaJobSupervisor;

    public MetaJobHandleService(MetaJobSupervisor metaJobSupervisor) {
        this.metaJobSupervisor = metaJobSupervisor;
    }

    @Override
    public void createJob(NameJob nameJob) {
        metaJobSupervisor.createJob(nameJob);
    }

    @Override
    public void startJob(String jobName) {
        metaJobSupervisor.startJob(jobName);
    }

    @Override
    public void stopJob(String jobName) {
        metaJobSupervisor.stopJob(jobName);
    }

    @Override
    public void removeJob(String jobName) {
        metaJobSupervisor.removeJob(jobName);
    }

    @Override
    public void reportJobHealth(JobHealth jobHealth) {
        metaJobSupervisor.reportJobHealth(jobHealth);
    }
}
