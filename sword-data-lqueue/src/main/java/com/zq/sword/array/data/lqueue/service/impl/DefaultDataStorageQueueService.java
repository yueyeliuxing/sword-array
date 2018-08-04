package com.zq.sword.array.data.lqueue.service.impl;


import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.manager.DataQueuePersistenceManager;
import com.zq.sword.array.data.lqueue.manager.MemoryDataQueueManager;
import com.zq.sword.array.data.lqueue.manager.impl.DataQueuePersistenceManagerImpl;
import com.zq.sword.array.data.lqueue.manager.impl.MemoryDataQueueManagerImpl;
import com.zq.sword.array.data.lqueue.service.DataStorageQueueService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public class DefaultDataStorageQueueService extends AbstractTaskService implements DataStorageQueueService {

    private MemoryDataQueueManager memoryDataQueueManager;

    private DataQueuePersistenceManager dataQueuePersistenceManager;

    @Override
    public void start(ServiceConfig serviceConfig) {
        String dataItemFilePath = serviceConfig.getProperty(NodeServerConfigKey.T_LEFT_DATA_ITEM_FILE_PATH);
        memoryDataQueueManager = new MemoryDataQueueManagerImpl();
        dataQueuePersistenceManager = new DataQueuePersistenceManagerImpl(dataItemFilePath);

        //初始化数据
        initData();

        //初始化任务
        initTasks();
    }

    /*
     *初始化数据
     */
    private void initData(){
        List<DataItem> dataItems = dataQueuePersistenceManager.loadDataItem();
        memoryDataQueueManager.load(dataItems);
    }

    /**
     * 初始化任务
     */
    private void initTasks(){
        //每隔一天重新合并数据文件
        loadTimedTask(()->{
            dataQueuePersistenceManager.resetDataItem(dataQueuePersistenceManager.loadDataItem());
        }, 1, TimeUnit.DAYS);
    }


    @Override
    public Long getLastDataItemId() {
        return memoryDataQueueManager.getLastDataItemId();
    }

    @Override
    public void addDataItem(DataItem item) {
        memoryDataQueueManager.addDataItem(item);
        dataQueuePersistenceManager.persistenceDataItem(item);
    }

    @Override
    public DataItem peekDataItem() {
        return memoryDataQueueManager.peekDataItem();
    }

    @Override
    public void removeDataItem(DataItem dataItem) {
        memoryDataQueueManager.removeDataItem(dataItem);
        dataQueuePersistenceManager.removeDataItem(dataItem);
    }

    @Override
    public List<DataItem> pollAfterId(Long id) {
        return null;
    }

    @Override
    public List<DataItem> pollAfterId(Long id, Integer maxNum) {
        return null;
    }


}
