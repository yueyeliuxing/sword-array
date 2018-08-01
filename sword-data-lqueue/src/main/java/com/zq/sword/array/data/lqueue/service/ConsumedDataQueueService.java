package com.zq.sword.array.data.lqueue.service;


import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.data.lqueue.domain.DataItem;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface ConsumedDataQueueService extends TaskService{

    /**
     * 添加数据
     * @param dataItem
     */
    void addDataItem(DataItem dataItem);

    /**
     * 获取数据
     */
    boolean containsDataItem(DataItem dataItem);

}
