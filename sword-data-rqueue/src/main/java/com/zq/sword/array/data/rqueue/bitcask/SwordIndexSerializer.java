package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.common.utils.JsonUtil;

import java.io.UnsupportedEncodingException;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordIndexSerializer implements SwordSerializer<SwordIndex> {

    @Override
    public byte[] serialize(SwordIndex swordIndex) {
     /*   ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putLong(swordIndex.getDataId());
        String fileId = swordIndex.getFileId();
        byte[] bytes = fileId.getBytes();
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.putLong(swordIndex.getValueLength());
        byteBuffer.putLong(swordIndex.getValuePosition());
        byteBuffer.putLong(swordIndex.getTimestamp());
        return byteBuffer.array();*/
        try {
            return JsonUtil.toJSONString(swordIndex).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
