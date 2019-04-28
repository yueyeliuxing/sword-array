package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskMonitor;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-24 16:37
 **/
public interface Task extends Actuator {

    /**
     * 任务状态
     * @return
     */
    int state();

    /**
     * 任务名称
     * @return
     */
    String name();

    /**
     * 设置任务监控器
     */
    void setTaskMonitor(TaskMonitor taskMonitor);

}
