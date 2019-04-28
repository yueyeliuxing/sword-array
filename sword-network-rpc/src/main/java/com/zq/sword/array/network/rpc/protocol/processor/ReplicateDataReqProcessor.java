package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.protocol.dto.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.data.ReplicateDataReq;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface ReplicateDataReqProcessor {

    /**
     * 获取指定消息
     * @param req
     * @return
     */
    List<ReplicateData> handleReplicateDataReq(ReplicateDataReq req);

    /**
     * 处理指定消息
     * @param replicateData
     */
    void handleReplicateData(ReplicateData replicateData);

    /**
     * 写入要消费下一个的offset
     * @param consumeNextOffset
     */
   void handleConsumeNextOffset(ConsumeNextOffset consumeNextOffset);
}
