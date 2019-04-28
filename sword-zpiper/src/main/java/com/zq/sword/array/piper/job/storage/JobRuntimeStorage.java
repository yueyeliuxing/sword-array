package com.zq.sword.array.piper.job.storage;

import com.zq.sword.array.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.piper.job.dto.ReplicateData;
import com.zq.sword.array.piper.job.dto.ReplicateDataReq;

import java.util.List;

/**
 * Job运行时存储
 */
public interface JobRuntimeStorage {

    /**
     * 写入数据
     * @param replicateData
     * @return
     */
    long writeReplicateData(ReplicateData replicateData);

    /***
     * 数据请求
     * @param req
     * @return
     */
    List<ReplicateData> readReplicateData(ReplicateDataReq req);

    /**
     * 获取消费的下一个索引信息
     * @param piperGroup
     * @param jobName
     * @return
     */
    long getConsumeNextOffset(String piperGroup, String jobName);

    /**
     * 写入已消费的下一个偏移量
     * @param consumeNextOffset
     */
    void writeConsumedNextOffset(ConsumeNextOffset consumeNextOffset);
}
