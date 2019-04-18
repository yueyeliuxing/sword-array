package com.zq.sword.array.stream.io.storage.engine;

import com.zq.sword.array.stream.io.storage.IndexableOffsetFileStorageEngine;
import com.zq.sword.array.stream.io.storage.IndexableOffsetStorageEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 追加日志存储引擎
 * @author: zhouqi1
 * @create: 2019-04-17 14:42
 **/
public class KVFileStorageEngine implements KVStorageEngine<byte[], byte[]> {

    private Logger logger = LoggerFactory.getLogger(KVFileStorageEngine.class);

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
    private IndexableOffsetStorageEngine<KVData> storageEngine;

    public KVFileStorageEngine(String storagePath) {
        this.storagePath = storagePath;
        this.storageEngine = new IndexableOffsetFileStorageEngine(storagePath, KVData.class);

    }

    @Override
    public boolean insert(byte[] key, byte[] value) {
        storageEngine.append(new KVData(key, value));
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
        KVData kvData = storageEngine.search("key", key);
        return kvData == null ? null : kvData.getValue();
    }
}
