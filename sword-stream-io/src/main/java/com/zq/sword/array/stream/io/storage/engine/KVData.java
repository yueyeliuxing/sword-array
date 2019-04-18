package com.zq.sword.array.stream.io.storage.engine;

import com.zq.sword.array.stream.io.serialize.RWStore;
import com.zq.sword.array.stream.io.storage.IndexableDataWritable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 日志数据
 * @author: zhouqi1
 * @create: 2019-04-17 14:53
 **/
@Data
@NoArgsConstructor
public class KVData implements IndexableDataWritable {

    private Logger logger = LoggerFactory.getLogger(KVData.class);

    /**
     * 主键
     */
    private byte[] key;

    /**
     * 数据内容
     */
    private byte[] value;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 数据校验crc
     */
    private String crc;

    public KVData(byte[] key, byte[] value) {
        this.key = key;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public long length() {
        return 4 + (key == null ? 0 : key.length) + 4 + (value == null ? 0 : value.length) + 8 + 4 + (crc == null ? 0 : crc.length());
    }

    @Override
    public void read(RWStore file) throws EOFException{
        try {
            int keySize = file.readInt();
            if(keySize > 0){
                key = new byte[keySize];
                file.read(key);
            }
            int valueSize = file.readInt();
            if(valueSize > 0){
                value = new byte[valueSize];
                file.read(value);
            }
            timestamp = file.readLong();
            int crcSize = file.readInt();
            if(crcSize > 0){
                byte[] crcBytes = new byte[crcSize];
                file.read(crcBytes);
                crc = new String(crcBytes);
            }
        }catch (EOFException e){
            throw e;
        }catch (IOException e) {
            logger.error("读文件 IO 异常", e);
        }
    }

    @Override
    public void write(RWStore file) {
        try {
            int keySize = key == null ? 0 : key.length;
            file.writeInt(keySize);
            if(keySize > 0){
                file.write(key);
            }
            int valueSize = value == null ? 0 : value.length;
            file.writeInt(valueSize);
            if(valueSize > 0){
                file.write(value);
            }
            file.writeLong(timestamp);
            int crcSize = crc == null ? 0 : crc.length();
            file.writeInt(crcSize);
            if(crcSize > 0){
                file.write(crc.getBytes());
            }
        } catch (IOException e) {
            logger.error("写文件 IO 异常", e);
        }
    }

    @Override
    public String[] indexMappings() {
        return new String[]{"key"};
    }

}
