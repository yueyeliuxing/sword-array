package com.zq.sword.array.rpc.api.namer.dto;

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
public class JobHealth extends NamePiper implements Serializable {
    private static final long serialVersionUID = 3949967165495904665L;

    public static final int NEW = 1;
    public static final int START = 2;
    public static final int STOP = 3;
    public static final int EXCEPTION = 4;

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

    public JobHealth(String name, int state) {
        this.name = name;
        this.state = state;
    }

    public JobHealth(String name, int state, String ex) {
        this.name = name;
        this.state = state;
        this.ex = ex;
    }

}
