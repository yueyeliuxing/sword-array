package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.stream.io.object.ObjectSerializer;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class OffsetMetaSerializer implements ObjectSerializer<OffsetMeta> {

    @Override
    public byte[] serialize(OffsetMeta offsetMeta) {
        int capacity = 8 + 8 + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.putLong(offsetMeta.getMsgId());
        byteBuffer.putLong(offsetMeta.getOffset());
        byteBuffer.putLong(offsetMeta.getDataLen());
        return byteBuffer.array();
    }
}
