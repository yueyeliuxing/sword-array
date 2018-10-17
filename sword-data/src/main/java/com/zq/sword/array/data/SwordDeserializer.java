package com.zq.sword.array.data;

/**
 * @program: sword-array
 * @description: 数据反序列化
 * @author: zhouqi1
 * @create: 2018-10-17 14:58
 **/
public interface SwordDeserializer<T extends Sword> {

    /**
     * 数据反序列化成SwordData
     * @param data 字节数组
     * @return SwordData
     */
    T deserialize(byte[] data);
}
