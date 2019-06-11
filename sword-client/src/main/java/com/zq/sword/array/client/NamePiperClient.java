package com.zq.sword.array.client;

import com.zq.sword.array.network.rpc.protocol.ClientNameProtocol;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameBranchJob;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.processor.ClientNameProcessor;

import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * @program: sword-array
 * @description: 客户端
 * @author: zhouqi1
 * @create: 2019-06-11 14:30
 **/
public class NamePiperClient implements PiperClient {

    private ClientNameProtocol clientNameProtocol;

    private SynchronousQueue<List<NamePiper>> piperQueue;

    private SynchronousQueue<NameJob> jobQueue;

    private SynchronousQueue<List<NameBranchJob>> branchJobQueue;

    public NamePiperClient(String nameLocation) {
        clientNameProtocol = new ClientNameProtocol(nameLocation);
        clientNameProtocol.setClientNameProcessor(new DefaultClientNameProcessor());

        piperQueue=  new SynchronousQueue();
        jobQueue=  new SynchronousQueue();
        branchJobQueue=  new SynchronousQueue();
    }

    @Override
    public List<NamePiper> allPiper() {
        clientNameProtocol.sendSearchPiper();
        return piperQueue.poll();
    }

    @Override
    public void createJob(NameJob job) {
        clientNameProtocol.createJob(job);
    }

    @Override
    public void startJob(String jobName) {
        clientNameProtocol.startJob(jobName);
    }

    @Override
    public void stopJob(String jobName) {
        clientNameProtocol.stopJob(jobName);
    }

    @Override
    public void removeJob(String jobName) {
        clientNameProtocol.removeJob(jobName);
    }

    @Override
    public NameJob getJob(String jobName) {
        clientNameProtocol.sendSearchJob(jobName);
        return jobQueue.poll();
    }

    @Override
    public List<NameBranchJob> listBranchJob(String jobName) {
        clientNameProtocol.sendSearchBranchJob(jobName);
        return branchJobQueue.poll();
    }

    private class DefaultClientNameProcessor extends ClientNameProcessor {
        @Override
        public void handlePipers(List<NamePiper> pipers) {
            piperQueue.add(pipers);
        }

        @Override
        public void handleJob(NameJob nameJob) {
            jobQueue.add(nameJob);
        }

        @Override
        public void handleBranchJob(List<NameBranchJob> branchJobs) {
            branchJobQueue.add(branchJobs);
        }
    }
}
