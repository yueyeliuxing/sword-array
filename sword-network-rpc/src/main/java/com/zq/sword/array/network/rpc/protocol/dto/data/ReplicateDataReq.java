package com.zq.sword.array.network.rpc.protocol.dto.data;

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
public class ReplicateDataReq implements Serializable{

    private String piperGroup;

    private String jobName;

    private long offset;

    private int reqSize;

    public ReplicateDataReq(String piperGroup, String jobName, long offset, int reqSize) {
        this.piperGroup = piperGroup;
        this.jobName = jobName;
        this.offset = offset;
        this.reqSize = reqSize;
    }
}
