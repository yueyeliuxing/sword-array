package com.zq.sword.array.common.data;

import com.zq.sword.array.common.data.Sword;
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
