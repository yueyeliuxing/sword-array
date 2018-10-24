package com.zq.sword.array.data.rqueue;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.data.Sword;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据服务
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
public interface RightRandomQueue<T extends Sword> {

    /**
     * 注册数据变化监听器
     * @param swordDataListener
     */
    void registerSwordDataListener(DataEventListener<T> swordDataListener);

    /**
     * 获取最新的id
     * @return
     */
    Long getLastSwordDataId();

    /**
     * 添加数据项
     * @param swordData
     */
    void push(T swordData);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @return 数据项
     */
    List<T> pollAfterId(Long id);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @param maxNum 最大获取的数目
     * @return 数据项
     */
    List<T> pollAfterId(Long id, Integer maxNum);
}
