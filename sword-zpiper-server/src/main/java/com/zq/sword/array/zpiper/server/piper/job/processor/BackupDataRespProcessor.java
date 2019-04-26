package com.zq.sword.array.zpiper.server.piper.job.processor;

import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataId;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface BackupDataRespProcessor {

    /**
     * 接收到已备份的数据
     * @param replicateDataId
     * @return
     */
    void backupReplicateData(ReplicateDataId replicateDataId);

    /**
     * 接收到已经同步完成的消费偏移量
     * @param consumeNextOffset
     */
   void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset);
}
