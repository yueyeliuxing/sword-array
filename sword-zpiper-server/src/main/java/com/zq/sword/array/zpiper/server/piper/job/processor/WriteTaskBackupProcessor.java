package com.zq.sword.array.zpiper.server.piper.job.processor;

import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;

/**
 * @program: sword-array
 * @description: 回调处理器
 * @author: zhouqi1
 * @create: 2019-04-24 20:42
 **/
public interface WriteTaskBackupProcessor {

    /**
     * 接收到已经同步完成的消费偏移量
     * @param consumeNextOffset
     */
   void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset);
}
