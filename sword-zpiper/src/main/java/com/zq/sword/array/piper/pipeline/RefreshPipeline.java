package com.zq.sword.array.piper.pipeline;

/**
 * @program: sword-array
 * @description: 可刷新的管道
 * @author: zhouqi1
 * @create: 2019-06-04 14:38
 **/
public interface RefreshPipeline<T> extends Pipeline<T> {

    /**
     * 刷新管道
     * @param config
     */
    void refresh(PipeConfig config);
}
