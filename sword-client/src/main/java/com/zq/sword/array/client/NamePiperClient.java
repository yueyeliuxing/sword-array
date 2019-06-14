package com.zq.sword.array.client;


import com.zq.sword.array.network.rpc.framework.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.RpcClient;
import com.zq.sword.array.rpc.api.namer.JobHandleService;
import com.zq.sword.array.rpc.api.namer.JobSearchService;
import com.zq.sword.array.rpc.api.namer.PiperSearchService;
import com.zq.sword.array.rpc.api.namer.dto.NameBranchJob;
import com.zq.sword.array.rpc.api.namer.dto.NameJob;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.List;

/**
 * @program: sword-array
 * @description: 客户端
 * @author: zhouqi1
 * @create: 2019-06-11 14:30
 **/
public class NamePiperClient implements PiperClient {

    private RpcClient rpcClient;

    private JobHandleService jobHandleService;

    private JobSearchService jobSearchService;

    private PiperSearchService piperSearchService;

    public NamePiperClient(String nameLocation) {
        String[] ps = nameLocation.split(":");
        rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));

        jobHandleService = (JobHandleService)rpcClient.getProxy(JobHandleService.class);
        jobSearchService = (JobSearchService)rpcClient.getProxy(JobSearchService.class);
        piperSearchService = (PiperSearchService)rpcClient.getProxy(PiperSearchService.class);

        rpcClient.start();
    }

    @Override
    public List<NamePiper> allPiper() {
        return piperSearchService.listPipers();
    }

    @Override
    public void createJob(NameJob job) {
        jobHandleService.createJob(job);
    }

    @Override
    public void startJob(String jobName) {
        jobHandleService.startJob(jobName);
    }

    @Override
    public void stopJob(String jobName) {
        jobHandleService.stopJob(jobName);
    }

    @Override
    public void removeJob(String jobName) {
        jobHandleService.removeJob(jobName);
    }

    @Override
    public NameJob getJob(String jobName) {
        return jobSearchService.getJob(jobName);
    }

    @Override
    public List<NameBranchJob> listBranchJob(String jobName) {
        return jobSearchService.listBranchJobOfJob(jobName);
    }
}
