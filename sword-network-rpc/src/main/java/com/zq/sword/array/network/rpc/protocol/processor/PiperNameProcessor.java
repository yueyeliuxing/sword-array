package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;
import com.zq.sword.array.network.rpc.framework.message.MessageType;
import com.zq.sword.array.network.rpc.framework.message.TransferMessage;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;

/**
 * @program: sword-array
 * @description: Job命令监听器
 * @author: zhouqi1
 * @create: 2019-04-28 09:48
 **/
public class PiperNameProcessor implements ProtocolProcessor {

    /**
     * 接收Job命令
     * @param command
     */
    public void acceptJobCommand(JobCommand command){

    }

    @Override
    public boolean canProcess(TransferMessage message) {
        return message.getHeader() != null
                && message.getHeader().getType() == MessageType.JOB_COMMAND_RESP.value();
    }

    @Override
    public TransferMessage process(TransferMessage message) {
        if(message.getHeader() != null && message.getHeader().getType() == MessageType.JOB_COMMAND_RESP.value()) {
            JobCommand jobCommand = (JobCommand)message.getBody();
            if(jobCommand == null){
                return null;
            }
            //监听器接收Job命令
            acceptJobCommand(jobCommand);
        }
        return null;
    }
}
