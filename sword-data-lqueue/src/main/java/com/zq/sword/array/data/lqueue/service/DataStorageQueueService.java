package com.zq.sword.array.data.lqueue.service;


import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.data.lqueue.domain.DataItem;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface DataStorageQueueService extends TaskService{

    /**
     * 添加数据
     * @param item
     */
    void addDataItem(DataItem item);

    /**
     * 获取数据
     */
    DataItem peekDataItem();

    /**
     * 删除数据
     */
    void removeDataItem(DataItem dataItem);
}
