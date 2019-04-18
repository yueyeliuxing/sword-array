package com.zq.sword.array.stream.io.serialize;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: DataFileDecorator
 * @author: zhouqi1
 * @create: 2019-04-17 16:03
 **/
public abstract class AbstractRWStore implements RWStore {
    
    protected RWStore rwStore;

    public AbstractRWStore(RWStore rwStore) {
        this.rwStore = rwStore;
    }

    @Override
    public void position(long pos) throws IOException {
        rwStore.position(pos);
    }

    @Override
    public long position() throws IOException {
        return rwStore.position();
    }

    @Override
    public long size() throws IOException {
        return rwStore.size();
    }

    /**
     * 读取数据之前处理
     */
    public void readBefore(){

    }

    @Override
    public int readInt() throws IOException {
        readBefore();
        return rwStore.readInt();
    }

    @Override
    public long readLong() throws IOException {
        readBefore();
        return rwStore.readLong();
    }

    @Override
    public int read() throws IOException {
        readBefore();
        return rwStore.read();
    }

    @Override
    public int read(byte[] data) throws IOException {
        readBefore();
        return rwStore.read(data);
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        readBefore();
        return rwStore.read(data, offset, len);
    }

    @Override
    public long available() throws IOException {
        return rwStore.available();
    }

    /**
     * 写入之前处理
     */
    public void writeBefore(){

    }

    @Override
    public void writeInt(int data) throws IOException {
        writeBefore();
        rwStore.writeInt(data);
    }

    @Override
    public void writeLong(long data) throws IOException {
        writeBefore();
        rwStore.writeLong(data);
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {
        writeBefore();
        rwStore.writeBytes(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        writeBefore();
        rwStore.write(data);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        writeBefore();
        rwStore.write(data, offset, len);
    }
    
    @Override
    public void close() throws IOException {
        rwStore.close();
    }
}
