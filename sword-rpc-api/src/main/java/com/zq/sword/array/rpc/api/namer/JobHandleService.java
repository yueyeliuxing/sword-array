package com.zq.sword.array.rpc.api.namer;

import com.zq.sword.array.rpc.api.namer.dto.NameJob;
import com.zq.sword.array.rpc.api.namer.dto.JobHealth;

/**
 * @program: sword-array
 * @description: 任务处理service
 * @author: zhouqi1
 * @create: 2019-06-14 13:55
 **/
public interface JobHandleService {

    /**
     * 处理客户端创建任务的请求
     * @param nameJob
     */
    void createJob(NameJob nameJob);

    /**
     * 处理客户端启动任务的请求
     * @param jobName
     */
    void startJob(String jobName);

    /**
     * 处理客户端暂停任务的请求
     * @param jobName
     */
    void stopJob(String jobName);

    /**
     * 处理客户端删除任务的请求
     * @param jobName
     */
    void removeJob(String jobName);

    /**
     * 上报任务健康信息
     * @param jobHealth
     */
    void reportJobHealth(JobHealth jobHealth);
}
