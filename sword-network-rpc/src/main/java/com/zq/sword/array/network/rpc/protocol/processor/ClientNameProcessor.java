package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameBranchJob;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;

import java.util.List;

/**
 * @program: sword-array
 * @description: Job命令监听器
 * @author: zhouqi1
 * @create: 2019-04-28 09:48
 **/
public class ClientNameProcessor implements ProtocolProcessor {

    /**
     * 处理pipers
     * @param pipers
     */
    public void handlePipers(List<NamePiper> pipers){

    }

    /**
     * 处理nameJob
     * @param nameJob
     */
    public void handleJob(NameJob nameJob){

    }

    /**
     * 处理branchJobs
     * @param branchJobs
     */
    public void handleBranchJob(List<NameBranchJob> branchJobs){

    }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && (message.getHeader().getType() == MessageType.CLIENT_SEARCH_PIPERS_RESULT.value()
                || message.getHeader().getType() == MessageType.CLIENT_SEARCH_JOB_RESULT.value()
                || message.getHeader().getType() == MessageType.CLIENT_SEARCH_BRANCH_JOB_RESULT.value());
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_PIPERS_RESULT.value()) {
            List<NamePiper> pipers = (List<NamePiper>)message.getBody();
            handlePipers(pipers);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_JOB_RESULT.value()) {
            NameJob nameJob = (NameJob)message.getBody();
            handleJob(nameJob);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_BRANCH_JOB_RESULT.value()) {
            List<NameBranchJob> branchJobs = (List<NameBranchJob>)message.getBody();
            handleBranchJob(branchJobs);
        }
        return null;
    }
}
