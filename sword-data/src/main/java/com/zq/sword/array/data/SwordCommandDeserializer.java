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
public class SwordCommandDeserializer implements SwordDeserializer<SwordCommand> {

    @Override
    public SwordCommand deserialize(byte[] data) {

        SwordCommand swordCommand = new SwordCommand();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        swordCommand.setType(byteBuffer.get());

        int keyLen = byteBuffer.getInt();
        byte[] keyBytes = new byte[keyLen];
        byteBuffer.get(keyBytes);
        swordCommand.setKey(new String(keyBytes));

        int valueLen = byteBuffer.getInt();
        byte[] valueBytes = new byte[valueLen];
        byteBuffer.get(valueBytes);
        swordCommand.setValue(new String(valueBytes));

        //swordCommand.setEx(byteBuffer.getInt());

        //swordCommand.setPx(byteBuffer.getLong());

        return swordCommand;
    }
}
