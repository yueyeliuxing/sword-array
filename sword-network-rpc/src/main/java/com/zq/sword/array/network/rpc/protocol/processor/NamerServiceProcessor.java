package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.protocol.dto.client.NameJob;
import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
import com.zq.sword.array.network.rpc.protocol.dto.piper.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;

/**
 * @program: sword-array
 * @description: piper处理器
 * @author: zhouqi1
 * @create: 2019-04-28 20:56
 **/
public interface NamerServiceProcessor {

    /**
     * 处理piper注册
     * @param namePiper
     */
    void handlePiperRegister(NamePiper namePiper);

    /**
     * 处理JobCommand 请求
     * @param namePiper
     */
    JobCommand handleJobCommandReq(NamePiper namePiper);

    /**
     * 上报
     * @param jobHealth
     */
    void handleTaskHealthReport(JobHealth jobHealth);

    /**
     * 处理客户端启动任务的请求
     * @param nameJob
     */
    void handleClientStartJobReq(NameJob nameJob);
}
