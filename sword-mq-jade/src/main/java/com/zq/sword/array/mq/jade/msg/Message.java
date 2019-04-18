package com.zq.sword.array.mq.jade.msg;

import com.zq.sword.array.stream.io.storage.DataFile;
import com.zq.sword.array.stream.io.serialize.DataWritable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @program: sword-array
 * @description: swd消息
 * @author: zhouqi1
 * @create: 2019-01-15 20:49
 **/
@Data
@ToString
@NoArgsConstructor
public class Message implements Serializable, DataWritable{

    private static final long serialVersionUID = 2079397312819633699L;

    private Logger logger = LoggerFactory.getLogger(Message.class);
    /**
     * 消息ID
     */
    private long msgId;

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String tag;

    /**
     * 消息体
     */
    private byte[] body;

    /**
     * 时间戳
     */
    private long timestamp;

    @Override
    public long length() {
        return 8 + 4 + ( topic == null ? 0 : topic.length()) + 4 + ( tag == null ? 0 : tag.length()) + 4 + ( body == null ? 0 : body.length) + 8;
    }

    @Override
    public void read(DataFile file) throws EOFException {
        try{
            msgId = file.readLong();
            int topicLen = file.readInt();
            if(topicLen > 0){
                byte[] topicBytes = new byte[topicLen];
                file.read(topicBytes);
                topic = new String(topicBytes);
            }

            int tagLen = file.readInt();
            if(tagLen > 0){
                byte[] tagBytes = new byte[tagLen];
                file.read(tagBytes);
                tag = new String(tagBytes);
            }

            int len = file.readInt();
            if(len > 0){
                byte[] bodyBytes = new byte[len];
                file.read(bodyBytes);
                body = bodyBytes;
            }
            timestamp = file.readLong();
        }catch (EOFException e){
            throw e;
        }catch (IOException e){
            logger.error("写入文件错误", e);
        }

    }

    @Override
    public void write(DataFile file) {
        try{
            file.writeLong(msgId);
            file.writeInt(topic.length());
            if(topic.length() > 0){
                file.write(topic.getBytes());
            }
            file.writeInt(tag.length());
            if(tag.length() > 0){
                file.write(tag.getBytes());
            }
            file.writeInt(body.length);
            if(body.length > 0){
                file.write(body);
            }
            file.writeLong(timestamp);
        }catch (IOException e){
            logger.error("写入文件错误", e);
        }
    }
}
