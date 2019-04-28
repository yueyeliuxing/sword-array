package com.zq.sword.array.zpiper.server.piper.job.monitor;

/**
 * @program: sword-array
 * @description: 监控器
 * @author: zhouqi1
 * @create: 2019-04-25 14:50
 **/
public interface TaskMonitor {

    /**
     * 监控健康度
     * @return
     */
    void monitor(TaskHealth health);
}
