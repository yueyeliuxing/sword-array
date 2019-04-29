package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;

import java.util.List;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface ConsumeDataRespProcessor {

    /**
     * 接收到请求的数据
     * @param replicateData
     */
    void consumeReplicateData(List<ReplicateData> replicateData);
}
