package com.zq.sword.array.data.lqueue;


import com.zq.sword.array.common.data.Sword;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface LeftOrderlyQueue<T extends Sword> {

    /**
     * 获取最新的id
     * @return
     */
    Long getLastDataId();

    /**
     * 添加数据
     * @param data
     */
    void push(T data);

    /**
     * 获取数据
     */
    T poll();

    /**
     * 数据是否已被消费成功
     */
    boolean containsConsumed(T data);

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
