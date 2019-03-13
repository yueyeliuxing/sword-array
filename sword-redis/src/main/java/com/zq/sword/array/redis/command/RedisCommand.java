package com.zq.sword.array.redis.command;

import lombok.Data;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @program: sword-array
 * @description: 命令数据
 * @author: zhouqi1
 * @create: 2018-10-18 19:46
 **/
@Data
@ToString
public class RedisCommand {

    public static final RedisCommand DELETE_COMMAND = new RedisCommand();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedisCommand that = (RedisCommand) o;
        return type == that.type &&
                Arrays.equals(key, that.key);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(super.hashCode(), type);
        result = 31 * result + Arrays.hashCode(key);
        return result;
    }
}
