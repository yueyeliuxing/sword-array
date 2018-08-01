package com.zq.sword.array.data.rqueue.service;

import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.data.rqueue.domain.DataIndex;

/**
 * @program: sword-array
 * @description: 数据索引服务
 * @author: zhouqi1
 * @create: 2018-07-26 10:05
 **/
public interface DataIndexService extends TaskService {

    /**
     * 添加数据索引
     * @param dataIndex
     */
    void addDataIndex(DataIndex dataIndex);

    /**
     * 获取数据索引
     * @param dataId
     * @return
     */
    DataIndex getDataIndex(Long dataId);
}
