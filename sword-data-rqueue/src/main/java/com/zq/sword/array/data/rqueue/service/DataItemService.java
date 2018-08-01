package com.zq.sword.array.data.rqueue.service;

import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.common.task.Task;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.domain.DataItem;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据索引服务
 * @author: zhouqi1
 * @create: 2018-07-26 10:05
 **/
public interface DataItemService extends TaskService {

    /**
     * 添加数据项
     * @param dataItem
     * @return
     */
    DataIndex addDataItem(DataItem dataItem);

    /**
     * 获取数据项
     * @param dataIndex
     * @return
     */
    List<DataItem> listDataItemAfterIndex(DataIndex dataIndex, Integer num);
}
