package com.zq.sword.array.data.swdmq.msg;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: swd消息
 * @author: zhouqi1
 * @create: 2019-01-15 20:49
 **/
@Data
@ToString
public class SwdMsg {

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
