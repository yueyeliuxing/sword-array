package com.zq.sword.array.data;

import com.zq.sword.array.common.event.DataEventListener;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据对列
 * @author: zhouqi1
 * @create: 2018-11-08 10:41
 **/
public interface DataQueue<T> {

    /**
     * 注册数据变化监听器
     * @param dataEventListener
     */
    void registerSwordDataListener(DataEventListener<T> dataEventListener);

    /**
     * 获取最新的id
     * @return
     */
    Long getLastDataId();

    /**
     * 添加数据
     * @param data
     */
    boolean push(T data);

    /**
     * 获取数据
     */
    T poll();

}
