package com.zq.sword.array.data.lqueue.service.impl;


import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.manager.ConsumedDataQueueManager;
import com.zq.sword.array.data.lqueue.manager.impl.ConsumedDataQueueManagerImpl;
import com.zq.sword.array.data.lqueue.service.ConsumedDataQueueService;

import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public class DefaultConsumedDataQueueService extends AbstractTaskService implements ConsumedDataQueueService {

    private ConsumedDataQueueManager consumedDataQueueManager;

    @Override
    public void start(ServiceConfig serviceConfig) {
        consumedDataQueueManager = new ConsumedDataQueueManagerImpl();

        initTasks();
    }

    private void initTasks(){
        loadTimedTask(()->{
            consumedDataQueueManager.clearDataItem();
        }, 2, TimeUnit.MINUTES);
    }

    @Override
    public void addDataItem(DataItem dataItem) {
        consumedDataQueueManager.addDataItem(dataItem);
    }

    @Override
    public boolean containsDataItem(DataItem dataItem) {
        return consumedDataQueueManager.containsDataItem(dataItem);
    }


}
