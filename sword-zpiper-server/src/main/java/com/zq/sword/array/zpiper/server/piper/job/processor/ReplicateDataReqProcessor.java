package com.zq.sword.array.zpiper.server.piper.job.processor;

import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;

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
    List<ReplicateData> obtainReplicateData(ReplicateDataReq req);

    /**
     * 处理指定消息
     * @param replicateData
     */
    void writeReplicateData(ReplicateData replicateData);

    /**
     * 写入要消费下一个的offset
     * @param consumeNextOffset
     */
   void writeConsumeNextOffset(ConsumeNextOffset consumeNextOffset);
}
