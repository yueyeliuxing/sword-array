package com.zq.sword.array.namer.job;

import com.zq.sword.array.namer.piper.MetaPiper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: sword-array
 * @description: 任务元数据
 * @author: zhouqi1
 * @create: 2019-04-28 19:21
 **/
@Data
public class BranchJob {

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
    private MetaPiper piper;

    /**
     * 需要备份的pipers
     */
    private List<MetaPiper> backupPipers;

    /**
     * 需要消费的piper
     */
    private List<MetaPiper> consumePipers;

    /**
     * Job 状态 1 创建完成 2 启动成功 3 停止 4发生异常 {@linkplain JobHealth}
     */
    private int jobState;

    /**
     * Job 异常信息
     */
    private String jobEx;

    /**
     * replicateTask 重启次数
     */
    private AtomicInteger jobResetCount = new AtomicInteger(0);


    public BranchJob(String name, String piperGroup, String sourceRedis) {
        this.name = name;
        this.piperGroup = piperGroup;
        this.sourceRedis = sourceRedis;
        this.backupPipers = new ArrayList<>();
        this.consumePipers = new ArrayList<>();
        jobState = 0;
    }

    /**
     * 添加备份piper
     * @param piper
     */
    public void addBackupPiper(MetaPiper piper){
        this.backupPipers.add(piper);
    }

    /**
     * 添加消费piper
     * @param piper
     */
    public void addConsumePiper(MetaPiper piper){
        this.consumePipers.add(piper);
    }

}
