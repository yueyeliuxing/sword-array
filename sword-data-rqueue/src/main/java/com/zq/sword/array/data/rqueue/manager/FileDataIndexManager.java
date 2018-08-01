package com.zq.sword.array.data.rqueue.manager;

import com.zq.sword.array.data.rqueue.domain.DataIndex;

import java.util.Date;
import java.util.List;

public interface FileDataIndexManager {

    /**
     * 索引数据持久化到文件
     * @param dataIndexs
     */
    void saveDataIndexFile(List<DataIndex> dataIndexs);

    /**
     * 拿到所有的索引数据
     * @return
     */
    List<DataIndex> listDataIndex();

    /**
     * 获取指定日期的索引文件
     * @param startDate
     * @return
     */
    List<DataIndex> listDataIndex(Date startDate);

    /**
     * 删除数据索引
     * @param date
     */
    void removeDataIndex(Date date);
}
