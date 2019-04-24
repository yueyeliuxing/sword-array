package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.common.utils.ByteUtils;
import com.zq.sword.array.common.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 顺序文件存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:13
 **/
public class IndexableOffsetBlockFile<T extends IndexableDataWritable> implements IndexableOffsetSeqFile<T> {

    private Logger logger = LoggerFactory.getLogger(IndexableOffsetBlockFile.class);

    private static final Map<String, IndexableOffsetBlockFile> blockFiles = new HashMap<>();

    /**
     * 索引文件目录名称
     */
    public static final String INDEX_FILE_DIR = "index";

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 数据文件
     */
    private OffsetSeqFile<T> dataFile;

    /**
     * 索引字段->索引文件映射
     */
    private Map<String, IndexFile> indexFileMappings;

    public static synchronized IndexableOffsetBlockFile get(String storagePath, Class dataType){
        IndexableOffsetBlockFile blockFile = blockFiles.get(storagePath);
        if(blockFile == null){
            blockFile = new IndexableOffsetBlockFile(storagePath, dataType);
        }
        return blockFile;
    }

    private IndexableOffsetBlockFile(String storagePath, Class<T> dataType) {
        this.storagePath = storagePath;
        this.dataFile = OffsetBlockFile.get(storagePath, dataType);
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
            IndexFile seqIndexFile = new IndexFile(indexFile);
            this.indexFileMappings.put(seqIndexFile.indexField(), seqIndexFile);
        }
    }

    private String getIndexFileDir() {
        return getStoragePath() + File.separator + INDEX_FILE_DIR;
    }

    @Override
    public T read(String indexField, byte[] indexKey) {
        IndexFile indexFile = indexFileMappings.get(indexField);
        if(indexFile == null){
            throw new RuntimeException(String.format("%s is not index", indexField));
        }
        Index index = indexFile.readObject(indexKey);
        if(index == null){
            return null;
        }
        return read(index.getOffset());
    }

    @Override
    public String getStoragePath() {
        return storagePath;
    }

    @Override
    public long write(T data) {
        long offset = dataFile.write(data);
        //添加索引文件
        String[] indexFields =  data.indexMappings();
        if(indexFields != null && indexFields.length > 0){
            for (String indexField : indexFields){
                IndexFile indexFile = indexFileMappings.get(indexField);
                if(indexFile == null){
                    indexFile = new IndexFile(getIndexFileDir(), indexField);
                    indexFileMappings.put(indexField, indexFile);
                }
                indexFile.writeObject(new Index(ByteUtils.primitiveType2ByteArray(ReflectUtils.getFieldValue(data, indexField)), offset));
            }
        }
        return 0;
    }

    @Override
    public T read(long offset) {
        return dataFile.read(offset);
    }

    @Override
    public List<T> read(long offset, int num) {
        return dataFile.read(offset, num);
    }
}
