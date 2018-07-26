package com.zq.sword.array.data.rqueue.manager;

import com.zq.sword.array.data.rqueue.domain.DataIndex;

public interface MemoryDataIndexManager {

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

    /**
     * 删除数据索引
     * @param date
     */
    void removeDataIndex(String date);
}
