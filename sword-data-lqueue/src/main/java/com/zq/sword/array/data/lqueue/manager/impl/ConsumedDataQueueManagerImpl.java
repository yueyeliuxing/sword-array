package com.zq.sword.array.data.lqueue.manager.impl;

import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.manager.ConsumedDataQueueManager;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: sword-array
 * @description: 文件数据索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class ConsumedDataQueueManagerImpl implements ConsumedDataQueueManager {

    private Set<DataItem> consumedDataItemSet;

    public ConsumedDataQueueManagerImpl() {
        consumedDataItemSet = new CopyOnWriteArraySet<>();
    }

    @Override
    public void addDataItem(DataItem dataItem) {
        consumedDataItemSet.add(dataItem);
    }

    @Override
    public boolean containsDataItem(DataItem dataItem) {
        return consumedDataItemSet.contains(dataItem);
    }

    @Override
    public void clearDataItem() {
        consumedDataItemSet.clear();
    }
}
