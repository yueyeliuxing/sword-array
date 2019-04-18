package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.stream.io.serialize.RWStore;
import com.zq.sword.array.stream.io.serialize.DataWritable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@ToString
@NoArgsConstructor
public class DataIndex implements DataWritable {

    private Logger logger = LoggerFactory.getLogger(DataIndex.class);

    /**
     * 主键
     */
    private String key;

    /**
     * 文件编号
     */
    private long fileSeq;

    /**
     * 数据在文件的位置
     */
    private long dataPos;

    public DataIndex(byte[] key, long fileSeq, long dataPos) {
        this.key = new String(key);
        this.fileSeq = fileSeq;
        this.dataPos = dataPos;
    }

    @Override
    public long length() {
        return 4 + (key == null ? 0 : key.length()) + 8 + 8;
    }

    @Override
    public void read(RWStore store) throws EOFException {
        try {
            int keySize = store.readInt();
            if(keySize > 0){
                byte[] keyArray = new byte[keySize];
                store.read(keyArray);
                key = new String(keyArray);
            }
            fileSeq = store.readLong();
            dataPos = store.readLong();
        } catch (EOFException e){
            throw e;
        }catch (IOException e) {
            logger.error("读文件 IO 异常", e);
        }
    }

    @Override
    public void write(RWStore store) {
        try {
            int keySize = key == null ? 0 : key.length();
            store.writeInt(keySize);
            if(keySize > 0){
                store.write(key.getBytes());
            }
            store.writeLong(fileSeq);
            store.writeLong(dataPos);
        } catch (IOException e) {
            logger.error("写文件 IO 异常", e);
        }
    }
}
