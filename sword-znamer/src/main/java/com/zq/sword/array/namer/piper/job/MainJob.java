package com.zq.sword.array.namer.piper.job;

import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 主干Job
 * @author: zhouqi1
 * @create: 2019-04-29 09:56
 **/
@Data
public class MainJob {

    /**
     * JobName
     */
    private String name;

    /**
     * 源redis
     */
    private Map<String, String> sourceRedis;

    /**
     * 分支job key->PiperGroup value->BranchJob
     */
    private Map<String, BranchJob> branchJobs;

    /**
     * 获取分支job
     * @param piperGroup
     * @return
     */
    public BranchJob getBranchJob(String piperGroup){
        return branchJobs.get(piperGroup);
    }

    /**
     * 获取所有分支Job
     * @return
     */
    public Collection<BranchJob> allBranchJobs(){
        return Collections.unmodifiableCollection(branchJobs.values());
    }

    /**
     * 添加BranchJob
     * @param branchJob
     */
    public void addBranchJob(BranchJob branchJob) {
        branchJobs.put(branchJob.getPiperGroup(), branchJob);
    }

    /**
     * 得到所有的BranchJob
     * @return
     */
    public Collection<BranchJob> getBranchJobs(){
        return Collections.unmodifiableCollection(branchJobs.values());
    }
}
