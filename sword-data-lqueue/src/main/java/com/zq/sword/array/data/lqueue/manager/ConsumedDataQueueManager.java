package com.zq.sword.array.data.lqueue.manager;

import com.zq.sword.array.data.lqueue.domain.DataItem;

public interface ConsumedDataQueueManager {

    /**
     * 添加数据
     * @param dataItem
     */
    void addDataItem(DataItem dataItem);

    /**
     * 获取数据
     */
    boolean containsDataItem(DataItem dataItem);

    /**
     * 清空数据
     */
    void clearDataItem();
}
