package com.zq.sword.array.stream.io.serialize;

import java.io.EOFException;

/**
 * @program: sword-array
 * @description: 数据可写的序列化接口
 * @author: zhouqi1
 * @create: 2019-04-17 15:48
 **/
public interface DataWritable {

    /**
     * 数据的长度
     * @return
     */
    long length();

    /**
     * 从存储中读取数据
     * @param store
     */
    void read(RWStore store) throws EOFException;

    /**
     * 写入到存储
     * @param store
     */
    void write(RWStore store);
}
