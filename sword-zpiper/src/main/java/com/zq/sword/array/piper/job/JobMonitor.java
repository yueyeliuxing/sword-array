package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.JobHealth;

/**
 * @program: sword-array
 * @description: 监控器
 * @author: zhouqi1
 * @create: 2019-04-25 14:50
 **/
public interface JobMonitor {

    /**
     * 监控健康度
     * @return
     */
    void monitor(JobHealth health);
}
