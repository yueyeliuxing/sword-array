package com.zq.sword.array.zpiper.server.piper.job;

/**
 * @program: sword-array
 * @description: Job环境
 * @author: zhouqi1
 * @create: 2019-04-26 14:36
 **/
public class JobEnv {

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


    public JobEnv(String name, String piperGroup, String sourceRedis) {
        this.name = name;
        this.piperGroup = piperGroup;
        this.sourceRedis = sourceRedis;
    }

    public String getName() {
        return name;
    }

    public String getPiperGroup() {
        return piperGroup;
    }

    public String getSourceRedis() {
        return sourceRedis;
    }
}
