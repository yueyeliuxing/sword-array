package com.zq.sword.array.admin.manager.impl;

import com.zq.sword.array.admin.manager.JobManager;
import com.zq.sword.array.client.PiperClient;
import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2019-06-11 15:42
 **/
@Component
public class JobManagerImpl implements JobManager {

    @Resource
    private PiperClient piperClient;


    @Override
    public void createJob(NameJob job) {
        piperClient.createJob(job);
    }

    @Override
    public void startJob(String jobName) {
        piperClient.startJob(jobName);
    }

    @Override
    public void stopJob(String jobName) {
        piperClient.stopJob(jobName);
    }

    @Override
    public void removeJob(String jobName) {
        piperClient.removeJob(jobName);
    }

    @Override
    public NameJob getJob(String jobName) {
        return piperClient.getJob(jobName);
    }

    @Override
    public List<NameBranchJob> listBranchJob(String jobName) {
        return piperClient.listBranchJob(jobName);
    }
}
