package com.zq.sword.array.data.lqueue.manager;

import com.zq.sword.array.data.lqueue.domain.DataItem;

import java.util.Date;
import java.util.List;

public interface DataQueuePersistenceManager {

    /**
     * 持久化数据
     * @param dataItems
     */
    void resetDataItem(List<DataItem> dataItems);

    /**
     * 持久化数据
     * @param dataItems
     */
    void persistenceDataItem(DataItem... dataItems);

    /**
     * 加载所有的数据
     * @return
     */
    List<DataItem> loadDataItem();

    /***
     * 删除数据
     * @param dataItem
     */
    void removeDataItem(DataItem dataItem);
}
