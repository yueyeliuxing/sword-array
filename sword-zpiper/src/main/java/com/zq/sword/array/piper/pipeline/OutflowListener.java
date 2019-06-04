package com.zq.sword.array.piper.pipeline;

/**
 * 输入数据监听器
 */
public interface OutflowListener<T> {

    /**
     * 监听数据
     * @param data
     */
    void outflow(T data);
}
