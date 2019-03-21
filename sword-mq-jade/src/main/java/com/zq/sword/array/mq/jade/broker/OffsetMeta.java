package com.zq.sword.array.mq.jade.broker;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 分段元数据
 * @author: zhouqi1
 * @create: 2019-03-11:04
 **/
@Data
@ToString
@NoArgsConstructor
public class OffsetMeta {

    private long msgId;

    private long offset;

    private long dataLen;

    public OffsetMeta(long msgId, long offset, long dataLen) {
        this.msgId = msgId;
        this.offset = offset;
        this.dataLen = dataLen;
    }
}
