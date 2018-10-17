package com.zq.sword.array.data;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class DefaultSwordDataDeserializer implements SwordDataDeserializer<DefaultSwordData> {

    @Override
    public DefaultSwordData deserialize(byte[] data) {
        DefaultSwordData swordData = new DefaultSwordData();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        swordData.setId(byteBuffer.getLong());
        int len = byteBuffer.getInt();
        byte[] valueBytes = new byte[len];
        byteBuffer.get(valueBytes);
        swordData.setValue(new String(valueBytes));
        swordData.setTimestamp(byteBuffer.getLong());
        int crcLen = byteBuffer.getInt();
        byte[] crcBytes = new byte[crcLen];
        byteBuffer.get(crcBytes);
        swordData.setCrc(new String(crcBytes));
        return swordData;
    }
}
