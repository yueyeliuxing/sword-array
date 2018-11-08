package com.zq.sword.array.redis;

/**
 * @program: sword-array
 * @description: 命令类型
 * @author: zhouqi1
 * @create: 2018-11-08 15:39
 **/
public enum  CommandType {
    SET(1),
    SET_EX(2),
    SET_NX(3),
    ;

    CommandType(int value) {
        this.value = (byte)value;
    }

    private byte value;

    public byte getValue() {
        return value;
    }
}
