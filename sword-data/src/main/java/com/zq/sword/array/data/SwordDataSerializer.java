package com.zq.sword.array.data;

import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordDataSerializer implements SwordSerializer<SwordData> {

    @Override
    public byte[] serialize(SwordData defaultSwordData) {

        Long id = defaultSwordData.getId();

        SwordSerializer<SwordCommand> swordCommandSwordSerializer = new  SwordCommandSerializer();
        SwordCommand value = defaultSwordData.getValue();
        byte[] bytes = swordCommandSwordSerializer.serialize(value);
        int valueLen = bytes.length;

        long timestamp  = defaultSwordData.getTimestamp();

        String crc = defaultSwordData.getCrc();
        byte[] crcBytes = crc.getBytes();
        int crcLen = crcBytes.length;

        int capacity = 16 + 8 + valueLen + 16 + 8 + crcLen;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.putLong(id);
        byteBuffer.putInt(valueLen);
        byteBuffer.put(bytes);
        byteBuffer.putLong(timestamp);
        byteBuffer.putInt(crcLen);
        byteBuffer.put(crcBytes);
        return byteBuffer.array();
    }
}
