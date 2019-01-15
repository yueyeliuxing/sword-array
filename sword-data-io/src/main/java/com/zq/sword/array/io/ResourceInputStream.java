package com.zq.sword.array.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 资源输入流
 * @author: zhouqi1
 * @create: 2019-01-14 10:24
 **/
public interface ResourceInputStream extends Closeable {

    /**
     * 跳转到指定索引处
     * @param offset
     */
    void skip(long offset) throws IOException;

    /**
     * 读int
     * @return
     */
    int readInt() throws IOException;

    /**
     * 读数据
     * @return
     */
    int read() throws IOException;

    /**
     * 读数据
     * @param data
     */
    int read(byte[] data) throws IOException;

    /**
     * 读数据
     * @param data
     * @param offset
     * @param len
     */
    int read(byte[] data, int offset, int len) throws IOException;

    /**
     * 可读数据量
     * @return
     */
    long available() throws IOException;
}
