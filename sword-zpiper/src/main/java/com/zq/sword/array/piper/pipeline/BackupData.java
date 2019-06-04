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
public class BackupData {

    /**
     * REDIS 指令数据
     */
    public static final int REPLICATE_DATA = 1;
    public static final int REPLICATE_DATA_RESP = 2;

    /**
     * REDIS 指令 消费
     */
    public static final int CONSUME_DATA = 3;
    public static final int CONSUME_DATA_RESP = 4;

    /**
     * 数据类型
     */
    private int type;

    /**
     * 数据
     */
    private Object data;

    public BackupData(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
