package com.zq.sword.array.network.rpc.protocol.processor;

import com.zq.sword.array.network.rpc.protocol.dto.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.data.ReplicateDataId;

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
