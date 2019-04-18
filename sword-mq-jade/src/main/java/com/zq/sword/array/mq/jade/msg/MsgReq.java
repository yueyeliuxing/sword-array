package com.zq.sword.array.mq.jade.msg;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 消息请求实体
 * @author: zhouqi1
 * @create: 2019-01-17 21:32
 **/
@Data
@ToString
@NoArgsConstructor
public class MsgReq implements Serializable{

    private long partId;

    private long offset;

    private int msgSize;

    public MsgReq(long partId, long offset, int msgSize) {
        this.partId = partId;
        this.offset = offset;
        this.msgSize = msgSize;
    }
}
