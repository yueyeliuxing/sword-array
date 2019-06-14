package com.zq.sword.array.rpc.api.namer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sword-array
 * @description: 分支Job
 * @author: zhouqi1
 * @create: 2019-06-11 15:03
 **/
@Data
@ToString
@NoArgsConstructor
public class NameBranchJob  implements Serializable {
    private static final long serialVersionUID = -7468013121709051018L;


    /**
     *  任务名称 唯一
     */
    private String name;

    /**
     * 任务所在piperGroup
     */
    private String piperGroup;

    /**
     * 源redis
     */
    private String sourceRedis;

    /**
     * 任务所在的piper
     */
    private NamePiper piper;

    /**
     * 需要备份的pipers
     */
    private List<NamePiper> backupPipers;

    /**
     * 需要消费的piper
     */
    private List<NamePiper> consumePipers;

    /**
     * Job 状态 1 创建完成 2 启动成功 3 停止 4发生异常 {@linkplain JobHealth}
     */
    private int jobState;

    /**
     * Job 异常信息
     */
    private String jobEx;

}
