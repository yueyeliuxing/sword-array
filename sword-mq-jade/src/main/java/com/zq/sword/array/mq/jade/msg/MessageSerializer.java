package com.zq.sword.array.mq.jade.msg;

import com.zq.sword.array.data.ObjectSerializer;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: 默认的数据序列化工具
 * @author: zhouqi1
 * @create: 2018-10-17 15:06
 **/
public class MessageSerializer implements ObjectSerializer<Message> {

    @Override
    public byte[] serialize(Message swdMsg) {

        long msgId = swdMsg.getMsgId();
        String topic = swdMsg.getTopic();
        String tag = swdMsg.getTag();
        byte[] body = swdMsg.getBody();
        long timestamp  = swdMsg.getTimestamp();


        int capacity = 8 + 4 + topic.length() + 4 + tag.length() + 4 + body.length + 8;

        ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
        byteBuffer.putLong(msgId);
        byteBuffer.putInt(topic.length());
        if(topic.length() > 0){
            byteBuffer.put(topic.getBytes());
        }
        byteBuffer.putInt(tag.length());
        if(tag.length() > 0){
            byteBuffer.put(tag.getBytes());
        }
        byteBuffer.putInt(body.length);
        if(body.length > 0){
            byteBuffer.put(body);
        }
        byteBuffer.putLong(timestamp);
        return byteBuffer.array();
    }
}
