package com.zq.sword.array.rpc.api.piper.dto;

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
public class ReplicateData implements Serializable {
    private static final long serialVersionUID = -2603635956907360685L;


    /**
     * piperGroup
     */
    private String piperGroup;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 当前数据偏移量
     */
    private long offset;

    /**
     * 实际的消息
     */
    private byte[] data;

    /**
     * 下一条数据的偏移量
     */
    private long nextOffset;


    public ReplicateData(String piperGroup, String jobName, byte[] data) {
        this.piperGroup = piperGroup;
        this.jobName = jobName;
        this.data = data;
    }

    public ReplicateData(String piperGroup, String jobName, long offset, byte[] data) {
        this.piperGroup = piperGroup;
        this.jobName = jobName;
        this.offset = offset;
        this.data = data;
    }

    public ReplicateData(String piperGroup, String jobName, byte[] data, long nextOffset) {
        this.piperGroup = piperGroup;
        this.jobName = jobName;
        this.data = data;
        this.nextOffset = nextOffset;
    }
}
