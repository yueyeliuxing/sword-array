package com.zq.sword.array.data.rqueue.bitcask.index;

import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.utils.JsonUtil;
import com.zq.sword.array.data.rqueue.bitcask.data.SwordData;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwordIndexDeserializer implements SwordDeserializer<SwordIndex> {

    @Override
    public SwordIndex deserialize(byte[] data) {
        /*SwordIndex swordIndex = new SwordIndex();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        swordIndex.setDataId(byteBuffer.getLong());
        int len = byteBuffer.getInt();
        byte[] fileIdBytes = new byte[len];
        byteBuffer.get(fileIdBytes);
        swordIndex.setFileId(new String(fileIdBytes));
        swordIndex.setValueLength(byteBuffer.getLong());
        swordIndex.setValuePosition(byteBuffer.getLong());
        swordIndex.setTimestamp(byteBuffer.getLong());
        return swordIndex;*/
        try {
            return JsonUtil.parse(new String(data, "utf-8"), SwordIndex.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
