package com.zq.sword.array.data;

import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 命令序列化帮助类
 * @author: zhouqi1
 * @create: 2018-10-10 15:41
 **/
public class SwordCommandSerializer implements SwordSerializer<SwordCommand> {

    @Override
    public byte[] serialize(SwordCommand commandSword) {

        Byte type = commandSword.getType();
        if(type == null){
            type = 0;
        }

        byte[] bytes = null;
        int keyLen = 0;
        String key = commandSword.getKey();
        if(key != null){
            bytes = key.getBytes();
            keyLen = bytes.length;
        }

        byte[] valueBytes = null;
        int valueLen = 0;
        String value = commandSword.getValue();
        if(value != null){
            valueBytes = value.getBytes();
            valueLen = valueBytes.length;
        }

        int capacity = 1 + 8 + keyLen + 8 + valueLen;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.put(type);
        byteBuffer.putInt(keyLen);
        if(bytes != null){
            byteBuffer.put(bytes);
        }
        byteBuffer.putInt(valueLen);
        if(valueBytes != null){
            byteBuffer.put(valueBytes);
        }

        //byteBuffer.putInt(commandSword.getEx());
        //byteBuffer.putLong(commandSword.getPx());

        return byteBuffer.array();
    }
}
