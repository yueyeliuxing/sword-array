package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.stream.io.ResourceOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @program: sword-array
 * @description: 文件资源输出流
 * @author: zhouqi1
 * @create: 2019-01-14 11:08
 **/
public class FileResourceOutputStream implements ResourceOutputStream {

    private File targetFile;

    private RandomAccessFile file;

    public FileResourceOutputStream(File file) throws IOException {
        openStream(file);
    }

    public FileResourceOutputStream(String filePath) throws IOException {
       this(filePath == null ? null : new File(filePath));
    }

    public void openStream(File file) throws IOException{
        boolean fileExists = false;
        while (!fileExists){
            try {
                this.targetFile = file;
                this.file = new RandomAccessFile(targetFile, "rw");
                skip(this.file.length());
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
    public void writeInt(int data) throws IOException {
        file.writeInt(data);
    }

    @Override
    public void writeBytes(byte[] data) throws IOException {
        file.write(data);
    }

    @Override
    public void write(byte[] data) throws IOException {
        file.write(data);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        file.write(data, offset, len);
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
