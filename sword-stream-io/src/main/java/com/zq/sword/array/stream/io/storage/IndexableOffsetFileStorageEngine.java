package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.common.utils.ByteUtils;
import com.zq.sword.array.common.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 顺序文件存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:13
 **/
public class IndexableOffsetFileStorageEngine<T extends IndexableDataWritable> extends OffsetFileStorageEngine<T> implements IndexableOffsetStorageEngine<T> {

    private Logger logger = LoggerFactory.getLogger(IndexableOffsetFileStorageEngine.class);

    /**
     * 索引文件目录名称
     */
    public static final String INDEX_FILE_DIR = "index";

    /**
     * 索引字段->索引文件映射
     */
    private Map<String, DataIndexFile> indexFileMappings;

    public IndexableOffsetFileStorageEngine(String storagePath, Class<T> dataType) {
        super(storagePath, dataType);
        this.indexFileMappings = new ConcurrentHashMap<>();

        //加载数据结构
        initIndexFile();
    }

    /**
     * 加载
     */
    private void initIndexFile() {
        //加载索引数据
        String indexFileDirPath = getIndexFileDir();
        File indexFileDir = new File(indexFileDirPath);
        if(!indexFileDir.exists()){
            return;
        }
        File[] indexFiles = indexFileDir.listFiles();
        if(indexFiles == null){
            return;
        }
        for (File indexFile : indexFiles){
            DataIndexFile seqIndexFile = new DataIndexFile(indexFile);
            this.indexFileMappings.putIfAbsent(seqIndexFile.indexField(), seqIndexFile);
        }
    }

    private String getIndexFileDir() {
        return getStoragePath() + File.separator + INDEX_FILE_DIR;
    }

    @Override
    public void afterAppend(DataBlockFile<T> dataFile, long position, T data) {
        //添加索引文件
        String[] indexFields =  data.indexMappings();
        if(indexFields != null && indexFields.length > 0){
            for (String indexField : indexFields){
                DataIndexFile indexFile = indexFileMappings.get(indexField);
                if(indexFile == null){
                    indexFile = new DataIndexFile(getIndexFileDir(), indexField);
                    indexFileMappings.put(indexField, indexFile);
                }
                indexFile.writeObject(new DataIndex(ByteUtils.primitiveType2ByteArray(ReflectUtils.getFieldValue(data, indexField)), dataFile.sequence(), position));
            }
        }
    }

    @Override
    public T search(String indexField, byte[] indexKey) {
        DataIndexFile indexFile = indexFileMappings.get(indexField);
        if(indexFile == null){
            throw new RuntimeException(String.format("%s is not index", indexField));
        }
        DataIndex index = indexFile.readObject(indexKey);
        if(index == null){
            return null;
        }
        return search(index.getFileSeq() + index.getDataPos());
    }

}
