package com.zq.sword.array.data.stream;

import com.zq.sword.array.data.*;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @program: sword-array
 * @description: 数据随机读写文件
 * @author: zhouqi1
 * @create: 2018-10-30 11:20
 **/
public class BitcaskRandomAccessFile<T extends Sword> implements Closeable {

    private SwordSerializer<T> swordSerializer;

    private SwordDeserializer<T> swordDeserializer;

    private DataSeparator dataSeparator;

    private RandomAccessFile randomAccessFile;

    public BitcaskRandomAccessFile(String name, String mode, DataSeparator dataSeparator, SwordSerializer<T> swordSerializer, SwordDeserializer<T> swordDeserializer)  throws FileNotFoundException {
        this.randomAccessFile = new RandomAccessFile(name, mode);
        this.dataSeparator = dataSeparator;
        this.swordSerializer = swordSerializer;
        this.swordDeserializer = swordDeserializer;
    }

    public T read() throws IOException{
        return read(0L);
    }

    public T read(long pos) throws IOException{
        randomAccessFile.seek(pos);
        //分隔符为空 默认是长度分隔
        if(dataSeparator == null){
            int itemLen = randomAccessFile.readInt();
            byte[] itemArray = new byte[itemLen];
            int l = randomAccessFile.read(itemArray);
            if(l != -1){
                return swordDeserializer.deserialize(itemArray);
            }
            return null;
        }

        byte[] temp = new byte[1024];
        int line = 0;
        StringBuilder sb = new StringBuilder();
        while ((line = randomAccessFile.read(temp)) != -1) {
            sb.append(new String(temp,0,line));
            byte[] data = sb.toString().getBytes();
            int index = dataSeparator.isBoundary(data);
            if(index > -1){
                byte[] item = dataSeparator.toDataArray(data);
                //文件指针回退
                randomAccessFile.seek(Long.valueOf(index+dataSeparator.character().length()));
                return swordDeserializer.deserialize(item);
            }
        }
        return null;
    }

    /**
     * 从指定的偏移位置写入数据
     * @param data 写入的数据
     * @return 写入数据在文件中的偏移量
     */
    public long write(T data) throws IOException {
        long pos = randomAccessFile.length();
        return write(pos, data);
    }

    /**
     * 从指定的偏移位置写入数据
     * @param pos 指定偏移位置
     * @param data 写入的数据
     * @return 写入数据在文件中的偏移量
     */
    public long write(long pos, T data) throws IOException{
        randomAccessFile.seek(pos);

        byte[] dataArray = swordSerializer.serialize(data);
        if(dataSeparator == null){
            randomAccessFile.writeInt(dataArray.length);
            randomAccessFile.write(dataArray);
            return pos;
        }

        //获取分隔符
        String character  = dataSeparator.character();
        randomAccessFile.write(dataArray);
        randomAccessFile.writeBytes(character);
        return pos;
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
