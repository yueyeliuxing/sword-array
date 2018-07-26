package com.zq.sword.array.data.rqueue.manager;

import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.domain.DataItem;

import java.util.Date;
import java.util.List;

public interface FileDataItemManager {

    DataIndex addDataItem(DataItem dataItems);

    List<DataItem> listDataItemAfterIndex(DataIndex dataIndex);

    List<DataItem> listDataItemAfterIndex(DataIndex dataIndex, Integer num);

    void removeDataIndex(Date date);
}
