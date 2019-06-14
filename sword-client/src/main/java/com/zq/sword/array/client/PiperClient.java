package com.zq.sword.array.client;

import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.List;

/**
 * @program: sword-array
 * @description: 客户端
 * @author: zhouqi1
 * @create: 2019-06-11 14:13
 **/
public interface PiperClient {

    /**
     *  获取所有piper信息
     * @return
     */
    List<NamePiper> allPiper();

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
