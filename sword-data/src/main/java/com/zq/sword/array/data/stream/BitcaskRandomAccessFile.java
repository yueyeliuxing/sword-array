package com.zq.sword.array.data.stream;

import com.zq.sword.array.data.*;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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

    public BitcaskRandomAccessFile(String name, String mode, SwordSerializer<T> swordSerializer, SwordDeserializer<T> swordDeserializer)  throws FileNotFoundException {
        this(name, mode, new CharacterDataSeparator(), swordSerializer, swordDeserializer);
    }

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
        List<T> datas = read(pos, 1);
        if(datas != null && !datas.isEmpty()){
            return datas.get(0);
        }
        return null;
    }

    public List<T> read(long pos, Integer num) throws IOException{
        int n = 1;
        randomAccessFile.seek(pos);
        List<T> datas = new ArrayList<T>();
        while (num == null || n <= num){
            //分隔符为空 默认是长度分隔
            if(dataSeparator == null){
                int itemLen = randomAccessFile.readInt();
                byte[] itemArray = new byte[itemLen];
                int l = randomAccessFile.read(itemArray);
                if(l > -1){
                    datas.add(swordDeserializer.deserialize(itemArray));
                    n++;
                }else {
                    break;
                }
            }else {
                byte[] temp = new byte[1024];
                int line = 0;
                StringBuilder sb = new StringBuilder();
                int p = 0;
                while ((line = randomAccessFile.read(temp)) != -1) {
                    sb.append(new String(temp,0,line));
                    byte[] data = sb.toString().getBytes();
                    int index = dataSeparator.isBoundary(data);
                    if(index > -1){
                        byte[] item = dataSeparator.toDataArray(data);
                        int len = index+dataSeparator.character().length();
                        if(len < data.length){
                            sb = new StringBuilder();
                            for(int i = len; i < data.length; i++){
                                sb.append(data[i]);
                            }
                        }
                        datas.add(swordDeserializer.deserialize(item));
                        n++;
                    }else {
                        break;
                    }
                }
                break;
            }
        }
        return datas;


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
        }else {
            //获取分隔符
            String character  = dataSeparator.character();
            randomAccessFile.write(dataArray);
            randomAccessFile.writeBytes(character);
        }
        return pos;
    }

    @Override
    public void close() throws IOException {
        randomAccessFile.close();
    }
}
