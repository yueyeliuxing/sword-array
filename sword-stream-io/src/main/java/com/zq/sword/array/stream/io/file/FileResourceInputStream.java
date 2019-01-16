package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.stream.io.ResourceInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @program: sword-array
 * @description: 文件资源输入流
 * @author: zhouqi1
 * @create: 2019-01-14 11:06
 **/
public class FileResourceInputStream implements ResourceInputStream {

    private Logger logger = LoggerFactory.getLogger(FileResourceInputStream.class);

    /**
     * 对应的文件
     */
    private RandomAccessFile file;

    public FileResourceInputStream(File file) throws IOException {
        boolean fileExists = false;
        while (!fileExists){
            try {
                this.file = new RandomAccessFile(file, "rw");
                fileExists = true;
            } catch (FileNotFoundException e) {
                file.createNewFile();
                fileExists = false;
            }
        }

    }

    @Override
    public void skip(long offset) throws IOException {
        file.seek(offset);
    }

    @Override
    public int readInt() throws IOException {
        return file.readInt();
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
    public void close() throws IOException {
        file.close();
    }
}
