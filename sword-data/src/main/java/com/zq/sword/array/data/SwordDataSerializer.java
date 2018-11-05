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
        if(id == null){
            id = 0L;
        }

        byte[] bytes = null;
        int valueLen = 0;

        SwordCommand value = defaultSwordData.getValue();
        if (value != null) {
            SwordSerializer<SwordCommand> swordCommandSwordSerializer = new  SwordCommandSerializer();
            bytes = swordCommandSwordSerializer.serialize(value);
            valueLen = bytes.length;
        }

        long timestamp  = defaultSwordData.getTimestamp();

        byte[] crcBytes = null;
        int crcLen = 0;
        String crc = defaultSwordData.getCrc();
        if(crc != null){
            crcBytes = crc.getBytes();
            crcLen = crcBytes.length;
        }


        int capacity = 16 + 8 + valueLen + 16 + 8 + crcLen;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.putLong(id);
        byteBuffer.putInt(valueLen);
        if(bytes != null){
            byteBuffer.put(bytes);
        }
        byteBuffer.putLong(timestamp);
        byteBuffer.putInt(crcLen);
        if(crcBytes != null){
            byteBuffer.put(crcBytes);
        }
        return byteBuffer.array();
    }
}
