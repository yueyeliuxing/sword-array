package com.zq.sword.array.stream.io;

import com.zq.sword.array.stream.io.object.ObjectOutputStream;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: 对象资源输出流
 * @author: zhouqi1
 * @create: 2019-01-14 12:42
 **/
public abstract class AbstractResourceOutputStream implements ObjectOutputStream {
    @Override
    public void skip(long offset) throws IOException {

    }

    @Override
    public void writeInt(int data) throws IOException {

    }

    @Override
    public void writeBytes(byte[] data) throws IOException {

    }

    @Override
    public void write(byte[] data) throws IOException {

    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {

    }
}
