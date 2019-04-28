package com.zq.sword.array.data.storage.store;

import java.io.Closeable;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 数据文件
 * @author: zhouqi1
 * @create: 2019-04-17 15:25
 **/
public interface RWStore extends Closeable {

    /**
     * 跳转到指定索引处
     * @param pos
     */
    void position(long pos) throws IOException;

    /**
     * 当前位置
     * @return
     */
    long position() throws IOException;

    /**
     * 文件大小
     * @return
     * @throws IOException
     */
    long size() throws IOException;

    /**
     * 读int
     * @return
     */
    int readInt() throws IOException;

    /**
     * 读long
     * @return
     * @throws IOException
     */
    long readLong() throws IOException;

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


    /**
     * 写入int
     * @param data
     * @throws IOException
     */
    void writeInt(int data) throws IOException;

    /**
     * 写入long
     * @param data
     * @throws IOException
     */
    void writeLong(long data) throws IOException;

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
