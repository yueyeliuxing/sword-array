package com.zq.sword.array.data;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

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
    private byte type;

    /**
     * redis 命令 key
     */
    private byte[] key;

    /**
     * LSET
     */
    private long index;

    /**
     * HSET
     */
    private byte[] field;

    /**
     * HMSET
     */
    private Map<byte[], byte[]> fields;

    /**
     * redis 命令序列化值
     */
    private byte[] value;

    /**
     * SET
     */
    private byte[][] members;

    private int ex;

    private long px;
}
