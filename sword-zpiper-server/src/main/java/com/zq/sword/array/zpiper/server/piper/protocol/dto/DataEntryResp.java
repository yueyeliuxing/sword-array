package com.zq.sword.array.zpiper.server.piper.protocol.dto;

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
public class DataEntryResp implements Serializable{

    private long seq;

    private long offset;

    public DataEntryResp(long seq, long offset) {
        this.seq = seq;
        this.offset = offset;
    }
}
