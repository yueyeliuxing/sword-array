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
public class DataEntryReq implements Serializable{

    private String partGroup;

    private String partName;

    private long offset;

    private int reqSize;

    public DataEntryReq(String partGroup, String partName, long offset, int reqSize) {
        this.partGroup = partGroup;
        this.partName = partName;
        this.offset = offset;
        this.reqSize = reqSize;
    }
}
