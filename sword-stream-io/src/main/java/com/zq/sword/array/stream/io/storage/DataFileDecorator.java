package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.stream.io.serialize.RWStore;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: DataFileDecorator
 * @author: zhouqi1
 * @create: 2019-04-17 16:03
 **/
public abstract class DataFileDecorator implements RWStore {
    
    protected RWStore dataFile;

    public DataFileDecorator(RWStore dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public void position(long pos) throws IOException {
        dataFile.position(pos);
    }

    @Override
    public long position() throws IOException {
        return dataFile.position();
    }

    @Override
    public long size() throws IOException {
        return dataFile.size();
    }

    /**
     * 读取数据之前处理
     */
    public void readBefore(){

    }

    @Override
    public int readInt() throws IOException {
        readBefore();
        return dataFile.readInt();
    }

    @Override
    public long readLong() throws IOException {
        readBefore();
        return dataFile.readLong();
    }

    @Override
    public int read() throws IOException {
        readBefore();
        return dataFile.read();
    }

    @Override
    public int read(byte[] data) throws IOException {
        readBefore();
        return dataFile.read(data);
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        readBefore();
        return dataFile.read(data, offset, len);
    }

    @Override
    public long available() throws IOException {
        return dataFile.available();
    }

    /**
     * 写入之前处理
     */
    public void writeBefore(){

    }

    @Override
    public void writeInt(int data) throws IOException {
        writeBefore();
        dataFile.writeInt(data);
    }

    @Override
    public void writeLong(long data) throws IOException {
        writeBefore();
        dataFile.writeLong(data);
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {
        writeBefore();
        dataFile.writeBytes(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        writeBefore();
        dataFile.write(data);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        writeBefore();
        dataFile.write(data, offset, len);
    }
    
    @Override
    public void close() throws IOException {
        dataFile.close();
    }
}
