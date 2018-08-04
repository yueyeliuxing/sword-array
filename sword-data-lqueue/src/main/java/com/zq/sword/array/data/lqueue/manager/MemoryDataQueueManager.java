package com.zq.sword.array.data.lqueue.manager;

import com.zq.sword.array.data.lqueue.domain.DataItem;

import java.util.List;

public interface MemoryDataQueueManager {

    /**
     * 获取最新的id
     * @return
     */
    Long getLastDataItemId();

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

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @return 数据项
     */
    List<DataItem> pollAfterId(Long id);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @param maxNum 最大获取的数目
     * @return 数据项
     */
    List<DataItem> pollAfterId(Long id, Integer maxNum);
}
