package com.zq.sword.array.zpiper.server.piper.job.command;

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
     * 任务类型 0 创建 1 销毁任务
     */
    private byte type;

    /**
     *  任务名称 唯一
     */
    private String name;

    /**
     * 源redis
     */
    private String sourceRedis;

    /**
     * 数据需复制到的其他Piper
     */
    private List<String> replicatePipers;

    /**
     * 目标piper  PiperGroup|PiperLocation
     * 从目标piper获取数据
     */
    private List<String> targetPipers;



}
