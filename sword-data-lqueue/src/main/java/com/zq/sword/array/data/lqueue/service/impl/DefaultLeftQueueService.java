package com.zq.sword.array.data.lqueue.service.impl;


import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.service.ConsumedDataQueueService;
import com.zq.sword.array.data.lqueue.service.DataStorageQueueService;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public class DefaultLeftQueueService extends AbstractService implements LeftQueueService {

    private DataStorageQueueService dataStorageQueueService;

    private ConsumedDataQueueService consumedDataQueueService;

    public DefaultLeftQueueService() {
        dataStorageQueueService = new DefaultDataStorageQueueService();
        consumedDataQueueService = new DefaultConsumedDataQueueService();
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        dataStorageQueueService.start(serviceConfig);
        consumedDataQueueService.start(serviceConfig);
    }


    @Override
    public Long getLastDataItemId() {
        return dataStorageQueueService.getLastDataItemId();
    }

    @Override
    public void addDataItem(DataItem item) {
        dataStorageQueueService.addDataItem(item);
    }

    @Override
    public DataItem peekDataItem() {
        return dataStorageQueueService.peekDataItem();
    }

    @Override
    public void removeDataItem(DataItem dataItem) {
        dataStorageQueueService.removeDataItem(dataItem);
    }

    @Override
    public void addConsumedDataItem(DataItem dataItem) {
        consumedDataQueueService.addDataItem(dataItem);
    }

    @Override
    public boolean containsConsumedDataItem(DataItem dataItem) {
        return consumedDataQueueService.containsDataItem(dataItem);
    }


}
