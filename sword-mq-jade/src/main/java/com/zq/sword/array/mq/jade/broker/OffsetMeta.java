package com.zq.sword.array.mq.jade.broker;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 分段元数据
 * @author: zhouqi1
 * @create: 2019-03-11:04
 **/
@Data
@ToString
public class OffsetMeta {

    private long offset;

    private long dataLen;

    public OffsetMeta(long offset, long dataLen) {
        this.offset = offset;
        this.dataLen = dataLen;
    }
}
