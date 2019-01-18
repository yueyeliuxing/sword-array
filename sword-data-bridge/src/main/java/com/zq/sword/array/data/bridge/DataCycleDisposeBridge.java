package com.zq.sword.array.data.bridge;


import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 数据桥梁
 * @author: zhouqi1
 * @create: 2018-10-22 15:53
 **/
public interface DataCycleDisposeBridge<T extends Serializable> {

    /**
     * 是否是循环数据
     * @param data
     * @return
     */
    boolean isCycleData(T data);

    /**
     * 添加循环数据
     * @param data
     * @return
     */
    boolean addCycleData(T data);


}
