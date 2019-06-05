package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.Header;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
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
     * 处理客户端启动任务的请求
     * @param nameJob
     */
    public void handleClientStartJobReq(NameJob nameJob){

    }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && (message.getHeader().getType() == MessageType.REGISTER_PIPER_REQ.value()
                || message.getHeader().getType() == MessageType.JOB_COMMAND_REQ.value()
                || message.getHeader().getType() == MessageType.REPORT_JOB_HEALTH.value()
                || message.getHeader().getType() == MessageType.CLIENT_START_JOB.value());
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
        }else if(message.getHeader() != null && message.getHeader().getType() == MessageType.CLIENT_START_JOB.value()) {
            NameJob nameJob = (NameJob)message.getBody();
            handleClientStartJobReq(nameJob);
        }
        return transferMessage;
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
