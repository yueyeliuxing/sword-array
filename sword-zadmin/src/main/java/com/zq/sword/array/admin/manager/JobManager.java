package com.zq.sword.array.admin.manager;


import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务处理器
 * @author: zhouqi1
 * @create: 2019-06-11 15:41
 **/
public interface JobManager {

    /**
     * 创建任务
     * @param job
     */
    void createJob(NameJob job);

    /**
     * 开启任务
     * @param jobName
     */
    void startJob(String jobName);

    /**
     * 暂停任务
     * @param jobName
     */
    void stopJob(String jobName);

    /**
     * 移除任务
     * @param jobName
     */
    void removeJob(String jobName);

    /**
     * 得到指定任务信息
     * @param jobName
     * @return
     */
    NameJob getJob(String jobName);

    /**
     * 得到所有分支任务
     * @param jobName
     * @return
     */
    List<NameBranchJob> listBranchJob(String jobName);

}
