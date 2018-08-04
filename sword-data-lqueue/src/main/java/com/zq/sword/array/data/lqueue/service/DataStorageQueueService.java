package com.zq.sword.array.data.lqueue.service;


import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.data.lqueue.domain.DataItem;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface DataStorageQueueService extends TaskService{

    /**
     * 获取最新的id
     * @return
     */
    Long getLastDataItemId();

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
