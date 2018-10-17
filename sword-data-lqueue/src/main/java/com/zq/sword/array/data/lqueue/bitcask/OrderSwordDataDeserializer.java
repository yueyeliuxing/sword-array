package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class OrderSwordDataDeserializer implements SwordDeserializer<OrderSwordData> {

    @Override
    public OrderSwordData deserialize(byte[] data) {
      /*  OrderSwordData swordData = new OrderSwordData();
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
            return JsonUtil.parse(new String(data, "utf-8"), OrderSwordData.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
