package com.zq.sword.array.zpiper.server.piper.job.monitor;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @program: sword-array
 * @description: 任务健康度
 * @author: zhouqi1
 * @create: 2019-04-25 14:52
 **/
@Data
@ToString
public class TaskHealth implements Serializable{
    private static final long serialVersionUID = 3949967165495904665L;

    /**
     * Job名称
     */
    private String jobName;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务状态
     */
    private int state;

    /**
     * 如果任务出错
     * 异常信息
     */
    private String ex;

    public TaskHealth(String jobName, String name, int state) {
        this.name = name;
        this.state = state;
    }

    public TaskHealth(String jobName, String name, int state, String ex) {
        this.name = name;
        this.state = state;
        this.ex = ex;
    }
}
