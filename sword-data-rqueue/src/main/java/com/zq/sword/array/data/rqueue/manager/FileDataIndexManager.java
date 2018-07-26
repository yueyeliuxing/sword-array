package com.zq.sword.array.data.rqueue.manager;

import com.zq.sword.array.data.rqueue.domain.DataIndex;

import java.util.Date;
import java.util.List;

public interface FileDataIndexManager {
    void saveDataIndexFile(List<DataIndex> dataIndexs);

    List<DataIndex> listDataIndex();

    List<DataIndex> listDataIndex(Date startDate);
}
