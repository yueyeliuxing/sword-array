package com.zq.sword.array.data;

import com.zq.sword.array.data.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordDataSerializer implements SwordSerializer<SwordData> {

    @Override
    public byte[] serialize(SwordData defaultSwordData) {
       /* ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putLong(defaultSwordData.getId());
        String value = defaultSwordData.getValue();
        byte[] bytes = value.getBytes();
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.putLong(defaultSwordData.getTimestamp());
        String crc = defaultSwordData.getCrc();
        byte[] crcBytes = crc.getBytes();
        byteBuffer.putInt(crcBytes.length);
        byteBuffer.put(crcBytes);
        return byteBuffer.array();*/
        try {
            return JsonUtil.toJSONString(defaultSwordData).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
