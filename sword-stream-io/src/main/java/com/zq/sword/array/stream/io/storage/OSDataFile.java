package com.zq.sword.array.stream.io.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @program: sword-array
 * @description: OS数据文件
 * @author: zhouqi1
 * @create: 2019-04-17 15:29
 **/
public class OSDataFile implements DataFile {

    /**
     * 对应的文件
     */
    private RandomAccessFile file;

    public OSDataFile(File file) {
        boolean fileExists = false;
        while (!fileExists){
            try {
                this.file = new RandomAccessFile(file, "rw");
                fileExists = true;
            } catch (FileNotFoundException e) {
                try {
                    if(!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                fileExists = false;
            }
        }
    }

    @Override
    public void position(long pos) throws IOException {
        file.seek(pos);
    }

    @Override
    public long position() throws IOException {
        return file.getFilePointer();
    }

    @Override
    public long size() throws IOException {
        return file.length();
    }

    @Override
    public int readInt() throws IOException {
        return file.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return file.readLong();
    }

    @Override
    public int read() throws IOException {
        return file.read();
    }

    @Override
    public int read(byte[] data) throws IOException {
        return file.read(data);
    }

    @Override
    public int read(byte[] data, int offset, int len) throws IOException {
        return file.read(data, offset, len);
    }

    @Override
    public long available() throws IOException {
        return file.length() - file.getFilePointer();
    }

    @Override
    public void writeInt(int data) throws IOException {
        lockWrite(new WriteCall() {
            @Override
            public long dataLen() {
                return 4;
            }

            @Override
            public void writeCall() throws IOException{
                file.writeInt(data);
            }
        });
    }

    @Override
    public void writeLong(long data) throws IOException {
        lockWrite(new WriteCall() {
            @Override
            public long dataLen() {
                return 8;
            }

            @Override
            public void writeCall() throws IOException{
                file.writeLong(data);
            }
        });
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {

        lockWrite(new WriteCall() {
            @Override
            public long dataLen() {
                return data.length;
            }

            @Override
            public void writeCall() throws IOException{
                file.write(data);
            }
        });
    }

    @Override
    public void write(byte[] data) throws IOException {
        lockWrite(new WriteCall() {
            @Override
            public long dataLen() {
                return data.length;
            }

            @Override
            public void writeCall() throws IOException{
                file.write(data);
            }
        });
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {

        lockWrite(new WriteCall() {
            @Override
            public long dataLen() {
                return len;
            }

            @Override
            public void writeCall() throws IOException{
                file.write(data, offset, len);
            }
        });
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    /**
     * 写加锁
     * @param call
     * @throws IOException
     */
    private void lockWrite(WriteCall call) throws IOException{
        FileChannel channel = file.getChannel();
        FileLock lock = channel.lock(position(), call.dataLen(), false);
        try{
            call.writeCall();
        }finally {
            lock.release();
        }
    }

    /**
     * 写回调
     */
    private interface WriteCall {

        /**
         * 数据长度
         */
        long dataLen();

        /**
         * 写回调
         */
        void writeCall()  throws IOException;
    }
}
