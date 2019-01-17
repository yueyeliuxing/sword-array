package com.zq.sword.array.data;

import com.zq.sword.array.common.event.HotspotEventListener;

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
    void registerSwordDataListener(HotspotEventListener<T> dataEventListener);

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

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @return 数据项
     */
    List<T> selectAfterId(Long id);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @param maxNum 最大获取的数目
     * @return 数据项
     */
    List<T> selectAfterId(Long id, Integer maxNum);
}
