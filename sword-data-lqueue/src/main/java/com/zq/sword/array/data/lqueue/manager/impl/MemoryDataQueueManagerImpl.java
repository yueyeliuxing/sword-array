package com.zq.sword.array.data.lqueue.manager.impl;

import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.manager.MemoryDataQueueManager;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @program: sword-array
 * @description: 内存数据索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class MemoryDataQueueManagerImpl implements MemoryDataQueueManager {

    private ConcurrentLinkedQueue<DataItem> dataItemQueue;

    private Long lastDataItemId;


    public MemoryDataQueueManagerImpl(){
        dataItemQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public Long getLastDataItemId() {
        return lastDataItemId;
    }

    @Override
    public void load(List<DataItem> dataItems){
        if(dataItems != null && !dataItems.isEmpty()){
            dataItems.forEach(dataItem -> {
                addDataItem(dataItem);
            });
        }
    }

    @Override
    public void addDataItem(DataItem dataItem){
        dataItemQueue.add(dataItem);
        lastDataItemId = dataItem.getId();
    }

    @Override
    public DataItem peekDataItem() {
        return dataItemQueue.peek();
    }

    @Override
    public void removeDataItem(DataItem dataItem) {
        dataItemQueue.remove();
    }
}
