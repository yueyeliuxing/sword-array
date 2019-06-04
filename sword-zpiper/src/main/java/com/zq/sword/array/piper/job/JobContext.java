package com.zq.sword.array.piper.job;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务上下文
 * @author: zhouqi1
 * @create: 2019-04-28 10:30
 **/
@Data
@ToString
public class JobContext {

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

    public JobContext(String name, String piperGroup, String sourceRedis, List<String> backupPipers, List<String> consumePipers) {
        this.name = name;
        this.piperGroup = piperGroup;
        this.sourceRedis = sourceRedis;
        this.backupPipers = backupPipers;
        this.consumePipers = consumePipers;
    }
}
