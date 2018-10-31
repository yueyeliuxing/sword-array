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

        String key = commandSword.getKey();
        byte[] bytes = key.getBytes();
        int keyLen = bytes.length;

        String value = commandSword.getValue();
        byte[] valueBytes = value.getBytes();
        int valueLen = valueBytes.length;


        int capacity = 1 + 8 + keyLen + 8 + valueLen;


        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.put(type);
        byteBuffer.putInt(keyLen);
        byteBuffer.put(bytes);
        byteBuffer.putInt(valueLen);
        byteBuffer.put(valueBytes);

        //byteBuffer.putInt(commandSword.getEx());
        //byteBuffer.putLong(commandSword.getPx());

        return byteBuffer.array();
    }
}
