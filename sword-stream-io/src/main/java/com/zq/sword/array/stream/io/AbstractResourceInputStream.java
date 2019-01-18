package com.zq.sword.array.stream.io;

import com.zq.sword.array.stream.io.object.ObjectInputStream;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: 对象输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:40
 **/
public abstract class AbstractResourceInputStream implements ObjectInputStream {


    @Override
    public void skip(long offset) throws IOException {

    }

    @Override
    public long offset() throws IOException {
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        return 0;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] data) throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        return 0;
    }

    @Override
    public long available() throws IOException {
        return 0;
    }
}
