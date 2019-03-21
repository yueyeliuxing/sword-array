package com.zq.sword.array.mq.jade.broker;


import com.zq.sword.array.stream.io.object.ObjectDeserializer;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class OffsetMetaDeserializer implements ObjectDeserializer<OffsetMeta> {

    @Override
    public OffsetMeta deserialize(byte[] data) {
        OffsetMeta offsetMeta = new OffsetMeta();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        offsetMeta.setMsgId(byteBuffer.getLong());
        offsetMeta.setOffset(byteBuffer.getLong());
        offsetMeta.setDataLen(byteBuffer.getLong());
        return offsetMeta;
    }
}
