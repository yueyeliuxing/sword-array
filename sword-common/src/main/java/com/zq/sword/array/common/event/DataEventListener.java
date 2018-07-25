package com.zq.sword.array.common.event;

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
