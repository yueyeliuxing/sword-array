package com.zq.sword.array.data.rqueue.bitcask.data;

import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordDataDeserializer implements SwordDeserializer<SwordData> {

    @Override
    public SwordData deserialize(byte[] data) {
      /*  SwordData swordData = new SwordData();
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
        return swordData;*/
        try {
            return JsonUtil.parse(new String(data, "utf-8"), SwordData.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
