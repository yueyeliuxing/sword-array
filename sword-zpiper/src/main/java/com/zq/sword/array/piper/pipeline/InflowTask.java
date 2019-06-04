package com.zq.sword.array.piper.pipeline;

/**
 * @program: sword-array
 * @description: 流入任务
 * @author: zhouqi1
 * @create: 2019-06-04 16:41
 **/
public interface InflowTask<T> {

    T execute(Object param);
}
