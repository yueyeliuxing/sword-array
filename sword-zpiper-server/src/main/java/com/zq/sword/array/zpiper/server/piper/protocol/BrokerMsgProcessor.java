package com.zq.sword.array.zpiper.server.piper.protocol;

import com.zq.sword.array.zpiper.server.piper.protocol.dto.LocatedDataEntry;
import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryReq;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface BrokerMsgProcessor {

    /**
     * 获取指定消息
     * @param req
     * @return
     */
    List<DataEntry> obtainMessages(DataEntryReq req);

    /**
     * 处理指定消息
     * @param message
     */
    void handleLocatedMessage(LocatedDataEntry message);
}
