package com.zq.sword.array.data;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class DefaultSwordDataSerialezer implements SwordDataSerializer<DefaultSwordData> {

    @Override
    public byte[] serialize(DefaultSwordData defaultSwordData) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putLong(defaultSwordData.getId());
        String value = defaultSwordData.getValue();
        byteBuffer.putInt(value.length());
        byteBuffer.put(value.getBytes());
        byteBuffer.putLong(defaultSwordData.getTimestamp());
        String crc = defaultSwordData.getCrc();
        byteBuffer.putInt(crc.length());
        byteBuffer.put(crc.getBytes());
        return byteBuffer.array();
    }
}
