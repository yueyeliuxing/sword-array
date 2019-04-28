package com.zq.sword.array.network.rpc.protocol.dto.command;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sword-array
 * @description: 任务命令
 * @author: zhouqi1
 * @create: 2019-04-24 17:04
 **/
@Data
@ToString
public class JobCommand implements Serializable{
    private static final long serialVersionUID = -5126628039718820768L;

    /***
     * 任务类型 {@linkplain JobType}
     */
    private byte type;

    /**
     *  任务名称 唯一
     */
    private String name;

    /**
     * 源Group
     */
    private String piperGroup;

    /**
     * 源redis
     */
    private String sourceRedis;

    /**
     * 备份的piper
     */
    private List<String> backupPipers;

    /**
     * 消费的piper  PiperGroup|PiperLocation
     * 从目标piper获取数据
     */
    private List<String> consumePipers;

    /**
     * 新增的复制piper
     */
    private List<String> incrementBackupPipers;

    /**
     * 减少的复制piper
     */
    private List<String> decreaseBackupPipers;

    /**
     * 新增的目标piper
     */
    private List<String> incrementConsumePipers;

    /**
     * 减少的目标piper
     */
    private List<String> decreaseConsumePipers;


}
