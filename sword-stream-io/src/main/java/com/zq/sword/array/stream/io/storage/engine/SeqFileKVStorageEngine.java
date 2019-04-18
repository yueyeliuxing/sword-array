package com.zq.sword.array.stream.io.storage.engine;

import com.zq.sword.array.stream.io.storage.SequentialFileStorage;
import com.zq.sword.array.stream.io.storage.SequentialStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 追加日志存储引擎
 * @author: zhouqi1
 * @create: 2019-04-17 14:42
 **/
public class SeqFileKVStorageEngine implements KVStorageEngine<byte[], byte[]> {

    private Logger logger = LoggerFactory.getLogger(SeqFileKVStorageEngine.class);

    /**
     * 删除的value
     */
    public static final byte[] DELETE_VALUE = {'-','-', '-'};

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 数据存储
     */
    private SequentialStorage<KVData> sequentialStorage;

    public SeqFileKVStorageEngine(String storagePath) {
        this.storagePath = storagePath;
        this.sequentialStorage = new SequentialFileStorage(storagePath, KVData.class);

    }

    @Override
    public boolean insert(byte[] key, byte[] value) {
        sequentialStorage.append(new KVData(key, value));
        return true;
    }

    @Override
    public boolean delete(byte[] key) {
        return insert(key, DELETE_VALUE);
    }

    @Override
    public boolean update(byte[] key, byte[] value) {
        return insert(key, value);
    }

    @Override
    public byte[] find(byte[] key) {
        KVData kvData = sequentialStorage.search("key", key);
        return kvData == null ? null : kvData.getValue();
    }
}
