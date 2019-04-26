package com.zq.sword.array.common.event;

/**
 * @program: sword-array
 * @description: 事件发射器
 * @author: zhouqi1
 * @create: 2019-01-14 09:52
 **/
public interface HotspotEventEmitter<T> {

    /**
     * 发射事件
     * @param dataEvent
     */
    void emitter(HotspotEvent dataEvent);

    /**
     * 注册事件监听器
     * @param dataEventListener
     */
    void registerEventListener(HotspotEventListener<T> dataEventListener);

    /**
     * 去除指定的事件监听器
     * @param dataEventListener
     */
    void removeEventListener(HotspotEventListener<T> dataEventListener);
}
