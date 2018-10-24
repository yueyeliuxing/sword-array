package com.zq.sword.array.data;

/**
 * @program: sword-array
 * @description: 数据序列化
 * @author: zhouqi1
 * @create: 2018-10-17 14:56
 **/
public interface SwordSerializer<T extends Sword> {

    /**
     * 序列化数据
     * @param t 数据
     * @return 字节数组
     */
    byte[] serialize(T t);
}
