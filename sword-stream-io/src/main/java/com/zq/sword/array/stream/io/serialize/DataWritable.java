package com.zq.sword.array.stream.io.serialize;

import com.zq.sword.array.stream.io.storage.DataFile;

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
     * 读入数据文件
     * @param file
     */
    void read(DataFile file) throws EOFException;

    /**
     * 写入数据
     * @param file
     */
    void write(DataFile file);
}
