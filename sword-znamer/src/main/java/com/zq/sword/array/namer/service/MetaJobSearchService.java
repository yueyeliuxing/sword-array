package com.zq.sword.array.namer.service;

import com.zq.sword.array.namer.job.MetaJobSupervisor;
import com.zq.sword.array.rpc.api.namer.JobSearchService;
import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;

import java.util.List;

/**
 * @program: sword-array
 * @description: job查询
 * @author: zhouqi1
 * @create: 2019-06-14 15:53
 **/
public class MetaJobSearchService implements JobSearchService {

    /**
     * Job管理器
     */
    private MetaJobSupervisor metaJobSupervisor;

    public MetaJobSearchService(MetaJobSupervisor metaJobSupervisor) {
        this.metaJobSupervisor = metaJobSupervisor;
    }

    @Override
    public NameJob getJob(String jobName) {
        return metaJobSupervisor.getNameJob(jobName);
    }

    @Override
    public List<NameBranchJob> listBranchJobOfJob(String jobName) {
        return metaJobSupervisor.listNameBranchJob(jobName);
    }
}
