package com.zq.sword.array.piper.pipeline;

import com.zq.sword.array.tasks.Task;

/**
 * @program: sword-array
 * @description: 任务定时流入
 * @author: zhouqi1
 * @create: 2019-06-04 14:38
 **/
public interface AutoInflowPipeline<T> extends RefreshPipeline<T> {

    /**
     * 数据流入
     * @param inflowTask
     */
    void inflow(InflowTask inflowTask);
}
