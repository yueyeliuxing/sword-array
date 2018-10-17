package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class OrderSwordDataSerializer implements SwordSerializer<OrderSwordData> {

    @Override
    public byte[] serialize(OrderSwordData defaultSwordData) {
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
