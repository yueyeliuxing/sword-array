package com.zq.sword.array.mq.jade.msg;


import com.zq.sword.array.data.ObjectDeserializer;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class SwdMsgDeserializer implements ObjectDeserializer<SwdMsg> {

    @Override
    public SwdMsg deserialize(byte[] data) {
        SwdMsg swdMsg = new SwdMsg();
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        Long id = byteBuffer.getLong();
        swdMsg.setMsgId(id == 0 ? null : id);

        int topicLen = byteBuffer.getInt();
        if(topicLen > 0){
            byte[] topicBytes = new byte[topicLen];
            byteBuffer.get(topicBytes);
            swdMsg.setTopic(new String(topicBytes));
        }

        int tagLen = byteBuffer.getInt();
        if(tagLen > 0){
            byte[] tagBytes = new byte[tagLen];
            byteBuffer.get(tagBytes);
            swdMsg.setTag(new String(tagBytes));
        }

        int len = byteBuffer.getInt();
        if(len > 0){
            byte[] bodyBytes = new byte[len];
            byteBuffer.get(bodyBytes);
            swdMsg.setBody(bodyBytes);
        }

        swdMsg.setTimestamp(byteBuffer.getLong());
        return swdMsg;
    }
}
