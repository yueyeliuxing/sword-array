package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameBranchJob;
import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;

import java.util.List;

/**
 * @program: sword-array
 * @description: piper处理器
 * @author: zhouqi1
 * @create: 2019-04-28 20:56
 **/
public class NamerServiceProcessor implements ProtocolProcessor {

    /**
     * 处理piper注册
     * @param namePiper
     */
    public void handlePiperRegister(NamePiper namePiper){

    }

    /**
     * 处理JobCommand 请求
     * @param namePiper
     */
    public JobCommand handleJobCommandReq(NamePiper namePiper){
        return null;
    }

    /**
     * 上报
     * @param jobHealth
     */
    public void handleTaskHealthReport(JobHealth jobHealth){

    }

    /**
     * 处理客户端创建任务的请求
     * @param nameJob
     */
    public void handleClientCreateJobReq(NameJob nameJob){

    }

    /**
     * 处理客户端启动任务的请求
     * @param jobName
     */
    public void handleClientStartJobReq(String jobName){

    }

    /**
     * 处理客户端暂停任务的请求
     * @param jobName
     */
    public void handleClientStopJobReq(String jobName){

    }

    /**
     * 处理客户端删除任务的请求
     * @param jobName
     */
    public void handleClientRemoveJobReq(String jobName){

    }

    /**
     * 处理客户端查询所有piper请求
     * @return
     */
    public List<NamePiper> handleClientSearchPipersReq() {
        return null;
    }

    /**
     * 处理客户端查询指定job
     * @param jobName
     * @return
     */
    public NameJob handleClientSearchJobReq(String jobName) {
        return null;
    }

    /**
     * 处理客户端查询指定job 所有分支job
     * @param jobName
     * @return
     */
    public List<NameBranchJob> handleClientSearchBranchJobReq(String jobName) {
        return null;
    }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && (message.getHeader().getType() == MessageType.REGISTER_PIPER_REQ.value()
                || message.getHeader().getType() == MessageType.JOB_COMMAND_REQ.value()
                || message.getHeader().getType() == MessageType.REPORT_JOB_HEALTH.value()
                || message.getHeader().getType() == MessageType.CLIENT_CREATE_JOB.value()
                || message.getHeader().getType() == MessageType.CLIENT_START_JOB.value()
                || message.getHeader().getType() == MessageType.CLIENT_STOP_JOB.value()
                || message.getHeader().getType() == MessageType.CLIENT_REMOVE_JOB.value()
                || message.getHeader().getType() == MessageType.CLIENT_SEARCH_PIPERS.value()
                || message.getHeader().getType() == MessageType.CLIENT_SEARCH_JOB.value()
                || message.getHeader().getType() == MessageType.CLIENT_SEARCH_BRANCH_JOB.value()
        );
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        TransferMessage transferMessage = null;
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.REGISTER_PIPER_REQ.value()) {
            NamePiper namePiper = (NamePiper)message.getBody();
            handlePiperRegister(namePiper);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.JOB_COMMAND_REQ.value()) {
            NamePiper namePiper = (NamePiper)message.getBody();
            JobCommand command = handleJobCommandReq(namePiper);
            if(command == null){
                return null;
            }
            transferMessage = buildJobCommandResp(command);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.REPORT_JOB_HEALTH.value()) {
            JobHealth jobHealth = (JobHealth) message.getBody();
            handleTaskHealthReport(jobHealth);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_CREATE_JOB.value()) {
            NameJob nameJob = (NameJob)message.getBody();
            handleClientCreateJobReq(nameJob);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_START_JOB.value()) {
            String jobName = (String)message.getBody();
            handleClientStartJobReq(jobName);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_START_JOB.value()) {
            String jobName = (String)message.getBody();
            handleClientStopJobReq(jobName);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_REMOVE_JOB.value()) {
            String jobName = (String)message.getBody();
            handleClientRemoveJobReq(jobName);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_PIPERS.value()) {
            List<NamePiper> pipers = handleClientSearchPipersReq();
            transferMessage = buildClientSearchPipersResp(pipers);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_JOB.value()) {
            String jobName = (String)message.getBody();
            NameJob nameJob = handleClientSearchJobReq(jobName);
            transferMessage = buildClientSearchJobResp(nameJob);
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_SEARCH_BRANCH_JOB.value()) {
            String jobName = (String)message.getBody();
            List<NameBranchJob> branchJobs = handleClientSearchBranchJobReq(jobName);
            transferMessage = buildClientSearchBranchJobResp(branchJobs);
        }
        return transferMessage;
    }

    private TransferMessage buildClientSearchJobResp(NameJob nameJob) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_JOB_RESULT.value());
        message.setHeader(header);
        message.setBody(nameJob);
        return message;
    }

    private TransferMessage buildClientSearchPipersResp(List<NamePiper> pipers) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_PIPERS_RESULT.value());
        message.setHeader(header);
        message.setBody(pipers);
        return message;
    }

    private TransferMessage buildClientSearchBranchJobResp(List<NameBranchJob> branchJobs) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.CLIENT_SEARCH_BRANCH_JOB_RESULT.value());
        message.setHeader(header);
        message.setBody(branchJobs);
        return message;
    }


    /**
     * 构建JobCommand 返回协议
     * @param command
     * @return
     */
    private TransferMessage buildJobCommandResp(JobCommand command) {
        TransferMessage message = new TransferMessage();
        Header header = new Header();
        header.setType(MessageType.JOB_COMMAND_RESP.value());
        message.setHeader(header);
        message.setBody(command);
        return message;
    }
}
