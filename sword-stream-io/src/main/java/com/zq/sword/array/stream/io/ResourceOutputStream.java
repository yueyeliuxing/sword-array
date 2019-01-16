package com.zq.sword.array.stream.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 资源输入流
 * @author: zhouqi1
 * @create: 2019-01-14 10:24
 **/
public interface ResourceOutputStream extends Closeable {

    /**
     * 跳转到指定索引处
     * @param offset
     */
    void skip(long offset) throws IOException;

    /**
     * 写入int
     * @param data
     * @throws IOException
     */
    void writeInt(int data) throws IOException;

    /**
     * 写入bytes
     * @param data
     * @throws IOException
     */
    void writeBytes(byte[] data) throws IOException;

    /**
     * 写数据
     * @param data
     * @return
     */
    void write(byte[] data) throws IOException;

    /**
     * 写入数据
     * @param data
     * @param offset
     * @param len
     * @return
     */
    void write(byte[] data, int offset, int len) throws IOException;
}
