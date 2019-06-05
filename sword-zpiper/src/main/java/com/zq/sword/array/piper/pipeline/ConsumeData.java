package com.zq.sword.array.piper.pipeline;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 备份数据
 * @author: zhouqi1
 * @create: 2019-06-04 14:54
 **/
@Data
@ToString
public class ConsumeData {

    /**
     * REDIS 指令数据
     */
    public static final int REPLICATE_DATA = 1;
    public static final int REPLICATE_DATA_REQ = 2;

    /**
     * 数据类型
     */
    private int type;

    /**
     * 数据
     */
    private Object data;

    public ConsumeData(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
