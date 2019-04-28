package com.zq.sword.array.piper.job.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 消费下一个的偏移量
 * @author: zhouqi1
 * @create: 2019-04-26 17:32
 **/
@Data
@ToString
public class ConsumeNextOffset implements Serializable {
    private static final long serialVersionUID = 6412033933778999274L;

    /**
     * piperGroup
     */
    private String piperGroup;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 下一条数据的偏移量
     */
    private long nextOffset;

    public ConsumeNextOffset(String piperGroup, String jobName, long nextOffset) {
        this.piperGroup = piperGroup;
        this.jobName = jobName;
        this.nextOffset = nextOffset;
    }
}
