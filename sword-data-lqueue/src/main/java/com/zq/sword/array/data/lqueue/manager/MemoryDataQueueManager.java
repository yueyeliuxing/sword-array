package com.zq.sword.array.data.lqueue.manager;

import com.zq.sword.array.data.lqueue.domain.DataItem;

import java.util.List;

public interface MemoryDataQueueManager {

    /**
     * 加载数据
     * @param dataItems
     */
    void load(List<DataItem> dataItems);

    /**
     * 添加数据索引
     * @param dataItem
     */
    void addDataItem(DataItem dataItem);

    /**
     * 获取数据
     */
    DataItem peekDataItem();

    /**
     * 删除数据
     */
    void removeDataItem(DataItem dataItem);
}
