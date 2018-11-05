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
public class SwordDataDeserializer implements SwordDeserializer<SwordData> {

    @Override
    public SwordData deserialize(byte[] data) {
        SwordData swordData = new SwordData();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        Long id = byteBuffer.getLong();
        swordData.setId(id == 0 ? null : id);

        int len = byteBuffer.getInt();
        swordData.setValue(SwordCommand.DELETE_COMMAND);
        if(len > 0){
            byte[] valueBytes = new byte[len];
            byteBuffer.get(valueBytes);
            SwordDeserializer<SwordCommand> swordCommandSwordDeserializer = new SwordCommandDeserializer();
            swordData.setValue(swordCommandSwordDeserializer.deserialize(valueBytes));
        }

        swordData.setTimestamp(byteBuffer.getLong());
        int crcLen = byteBuffer.getInt();
        if(crcLen > 0){
            byte[] crcBytes = new byte[crcLen];
            byteBuffer.get(crcBytes);
            swordData.setCrc(new String(crcBytes));
        }
        return swordData;
    }
}
