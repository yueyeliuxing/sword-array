package com.zq.sword.array.zpiper.server.piper.protocol.processor;

import com.zq.sword.array.zpiper.server.piper.job.dto.JobCommand;

/**
 * @program: sword-array
 * @description: Job命令监听器
 * @author: zhouqi1
 * @create: 2019-04-28 09:48
 **/
public interface JobCommandProcessor {

    /**
     * 接收Job命令
     * @param command
     */
    void accept(JobCommand command);
}
