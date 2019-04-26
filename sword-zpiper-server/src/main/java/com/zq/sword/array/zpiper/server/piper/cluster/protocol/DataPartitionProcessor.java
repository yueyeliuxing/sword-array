package com.zq.sword.array.zpiper.server.piper.cluster.protocol;

import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.LocatedDataEntry;
import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.DataEntryReq;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface DataPartitionProcessor {

    /**
     * 获取指定消息
     * @param req
     * @return
     */
    List<DataEntry> obtainDataEntries(DataEntryReq req);

    /**
     * 处理指定消息
     * @param message
     */
    void handleLocatedEntry(LocatedDataEntry message);
}
