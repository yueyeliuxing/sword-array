package com.zq.sword.array.namer.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;
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
     * replicateTask 状态 1 创建完成 2 启动成功 3 停止 4发生异常 {@linkplain com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskHealth}
     */
    private int replicateTaskState;

    /**
     * replicateTask 异常信息
     */
    private String replicateTaskEx;

    /**
     * replicateTask 重启次数
     */
    private AtomicInteger replicateTaskResetCount = new AtomicInteger(0);

    /**
     * writeTask 状态 1 创建完成 2 启动成功 3 停止 4发生异常 {@linkplain com.zq.sword.array.network.rpc.protocol.dto.piper.monitor.TaskHealth}
     */
    private int writeTaskState;

    /**
     * writeTask信息
     */
    private String writeTaskEx;

    /**
     * WriteTask 重启次数
     */
    private AtomicInteger writeTaskResetCount = new AtomicInteger(0);


    public BranchJob(String name, String piperGroup, String sourceRedis) {
        this.name = name;
        this.piperGroup = piperGroup;
        this.sourceRedis = sourceRedis;
        this.backupPipers = new ArrayList<>();
        this.consumePipers = new ArrayList<>();
        replicateTaskState = 0;
        writeTaskState = 0;
    }

    /**
     * 添加备份piper
     * @param piper
     */
    public void addBackupPiper(NamePiper piper){
        this.backupPipers.add(piper);
    }

    /**
     * 添加消费piper
     * @param piper
     */
    public void addConsumePiper(NamePiper piper){
        this.consumePipers.add(piper);
    }

}
