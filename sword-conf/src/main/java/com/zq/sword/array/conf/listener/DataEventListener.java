package com.zq.sword.array.conf.listener;

/**
 * 数据改变时的监听器
 */
public interface DataEventListener<T> {

    /**
     * 监听
     * @param dataEvent
     */
    void listen(DataEvent<T> dataEvent);
}
