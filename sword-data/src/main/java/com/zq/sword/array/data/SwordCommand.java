package com.zq.sword.array.data;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 命令数据
 * @author: zhouqi1
 * @create: 2018-10-18 19:46
 **/
@Data
@ToString
public class SwordCommand extends Sword {

    public static final SwordCommand DELETE_COMMAND = new SwordCommand();

    /**
     * 类型
     */
    private Byte type;

    /**
     * redis 命令 key
     */
    private String key;

    /**
     * redis 命令序列化值
     */
    private String value;

    private Integer ex;

    private Long px;
}
