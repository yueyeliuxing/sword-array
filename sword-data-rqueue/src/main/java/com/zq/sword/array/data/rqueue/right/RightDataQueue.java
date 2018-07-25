package com.zq.sword.array.data.rqueue.right;

import com.zq.sword.array.data.rqueue.item.DataItem;
import com.zq.sword.array.data.rqueue.item.DataItemId;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据服务
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
public interface RightDataQueue {

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

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @return 数据项
     */
    List<DataItem> pollAfterId(DataItemId id);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @param maxNum 最大获取的数目
     * @return 数据项
     */
    List<DataItem> pollAfterId(DataItemId id, int maxNum);
}
