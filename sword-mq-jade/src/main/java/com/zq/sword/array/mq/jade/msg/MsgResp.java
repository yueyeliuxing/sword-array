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
public class MsgResp implements Serializable{

    private long msgId;

    private long offset;

    public MsgResp(long msgId, long offset) {
        this.msgId = msgId;
        this.offset = offset;
    }
}
