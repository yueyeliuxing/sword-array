package com.zq.sword.array.mq.jade.msg;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class Message implements Serializable{

    private static final long serialVersionUID = 2079397312819633699L;
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
}
