package com.zq.sword.array.common.event;

/**
 * 数据改变时的监听器
 */
public interface HotspotEventListener<T> {

    /**
     * 监听
     * @param dataEvent
     */
    void listen(HotspotEvent<T> dataEvent);
}
