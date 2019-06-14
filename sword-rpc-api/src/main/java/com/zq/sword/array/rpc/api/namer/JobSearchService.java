package com.zq.sword.array.rpc.api.namer;

import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务查询服务
 * @author: zhouqi1
 * @create: 2019-06-14 14:04
 **/
public interface JobSearchService {

    /**
     * 处理客户端查询指定job
     * @param jobName
     * @return
     */
    NameJob getJob(String jobName);

    /**
     * 处理客户端查询指定job 所有分支job
     * @param jobName
     * @return
     */
    List<NameBranchJob> listBranchJobOfJob(String jobName);
}
