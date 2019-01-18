package com.zq.sword.array.mq.jade.msg;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 已经定位的消息
 * @author: zhouqi1
 * @create: 2019-01-18 09:47
 **/
@Data
@ToString
@NoArgsConstructor
public class LocatedMessage implements Serializable {
    private static final long serialVersionUID = -2603635956907360685L;

    /**
     * 分片ID
     */
    private long partId;

    /**
     * 实际的消息
     */
    private Message message;

    public LocatedMessage(long partId, Message message) {
        this.partId = partId;
        this.message = message;
    }
}
