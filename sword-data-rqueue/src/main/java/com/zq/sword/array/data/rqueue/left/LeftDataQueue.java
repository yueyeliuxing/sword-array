package com.zq.sword.array.data.rqueue.left;

import com.zq.sword.array.data.rqueue.item.DataItem;

/**
 * @program: sword-array
 * @description: 数据服务
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
public interface LeftDataQueue {

    /**
     * 添加数据项
     * @param dataItem
     */
    void push(DataItem dataItem);

    /**
     * 获取最新的数据项
     * @return 数据项
     */
    DataItem poll();

}
