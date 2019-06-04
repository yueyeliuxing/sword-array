package com.zq.sword.array.piper.pipeline;

/**
 * @program: sword-array
 * @description: IO通用接口
 * @author: zhouqi1
 * @create: 2019-06-03 10:08
 **/
public interface Pipeline<T> {

    /**
     * 打开
     */
    void open();

    /**
     * 流出数据异步监听
     * @param listener
     */
    void outflow(OutflowListener<T> listener);

    /**
     * 数据流入
     * @param data
     */
    void inflow(T data);

    /**
     * 关闭
     */
    void close();

}
