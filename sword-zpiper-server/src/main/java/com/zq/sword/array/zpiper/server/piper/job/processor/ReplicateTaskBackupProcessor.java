package com.zq.sword.array.zpiper.server.piper.job.processor;

import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataId;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface ReplicateTaskBackupProcessor {

    /**
     * 接收到已备份的数据
     * @param replicateDataId
     * @return
     */
    void backupReplicateData(ReplicateDataId replicateDataId);
}
