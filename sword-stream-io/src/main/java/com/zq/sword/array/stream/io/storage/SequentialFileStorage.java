package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.common.utils.ByteUtils;
import com.zq.sword.array.common.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @program: sword-array
 * @description: 顺序文件存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:13
 **/
public class SequentialFileStorage<T extends IndexableDataWritable> implements SequentialStorage<T>{

    private Logger logger = LoggerFactory.getLogger(SequentialFileStorage.class);

    /**
     * 数据文件目录名称
     */
    public static final String DATA_FILE_DIR = "data";

    /**
     * 索引文件目录名称
     */
    public static final String INDEX_FILE_DIR = "index";

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 数据类型
     */
    private Class<T> dataType;

    /**
     * 数据文件
     */
    private Map<Long, SeqDataFile<T>> dataFiles;

    /**
     * 当前数据文件
     */
    private volatile SeqDataFile<T> currentDataFile;

    /**
     * 索引字段->索引文件映射
     */
    private Map<String, SeqIndexFile> indexFileMappings;

    /**
     * 文件读写锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 写锁
     */
    private Lock writeLock = lock.writeLock();

    /**
     * 读锁
     */
    private Lock readLock = lock.readLock();

    public SequentialFileStorage(String storagePath, Class<T> dataType) {
        this.storagePath = storagePath;
        this.dataType = dataType;
        this.dataFiles = new ConcurrentHashMap<>();
        this.indexFileMappings = new ConcurrentHashMap<>();

    }

    @Override
    public long append(T data) {
        long offset;
        writeLock.lock();
        try{
            //如果当前文件还没有创建 或者 当前文件已写满 需要重新创建
            if(currentDataFile == null || currentDataFile.isFull()){
                SeqDataFile<T> preDataFile = currentDataFile;
                currentDataFile = new SeqDataFile(storagePath + File.separator + DATA_FILE_DIR, preDataFile == null ? "0" : preDataFile.size()+"");
                dataFiles.put(currentDataFile.sequence(), currentDataFile);
                if(currentDataFile.isFull()){
                    preDataFile.next(currentDataFile);
                }
            }
            offset = currentDataFile.position();
            currentDataFile.writeObject(data);

            //添加索引文件
            String[] indexFields =  data.indexMappings();
            if(indexFields != null && indexFields.length > 0){
                for (String indexField : indexFields){
                    SeqIndexFile indexFile = indexFileMappings.get(indexField);
                    if(indexFile == null){
                        indexFile = new SeqIndexFile(storagePath + File.separator + INDEX_FILE_DIR, indexField);
                        indexFileMappings.put(indexField, indexFile);
                    }
                    indexFile.writeObject(new SeqIndex(ByteUtils.primitiveType2ByteArray(ReflectUtils.getFieldValue(data, indexField)), currentDataFile.sequence(), offset));
                }
            }
        }catch (IOException e){
            logger.error("写入文件异常", e);
            throw new RuntimeException(e);
        }finally {
            writeLock.unlock();
        }
        return offset;
    }

    @Override
    public T search(long offset) {
        SeqDataFile<T> dataFile = searchDataFile(offset);
        long currentFilePos = offset - dataFile.sequence();
        return doSearch(dataFile, currentFilePos);
    }

    /**
     * 搜索
     * @param dataFile
     * @param currentFilePos
     * @return
     */
    private T doSearch(SeqDataFile<T> dataFile, long currentFilePos){
        T data = null;
        if(dataFile.isCurrent()){
            readLock.lock();
        }
        try{
            dataFile.position(currentFilePos);
            data = dataFile.readObject(dataType);
        }catch (IOException e){
            logger.error("读取文件失败", e);
            return null;
        }finally {
            if(dataFile.isCurrent()){
                readLock.unlock();
            }

        }
        return data;
    }

    /**
     * 根据偏移量获取指定数据文件
     * @param offset
     * @return
     */
    private SeqDataFile<T> searchDataFile(long offset) {
        SeqDataFile<T> targetDataFile = null;
        for(SeqDataFile<T> dataFile : dataFiles.values()){
            if(offset >= dataFile.sequence()){
                targetDataFile = dataFile;
                break;
            }
        }
        while (targetDataFile.next() != null && targetDataFile.next().sequence() <= offset){
            targetDataFile = targetDataFile.next();
        }
        return targetDataFile;
    }

    @Override
    public List<T> search(long offset, int num) {
        SeqDataFile<T> dataFile = searchDataFile(offset);
        long currentFilePos = offset - dataFile.sequence();
        List<T> datas = new ArrayList<>(num);
        //如果是当前文件 那就剩余数据都从当前文件中读
        if(dataFile.isCurrent()){
            readCurrentLogDataFile(dataFile, currentFilePos, num, datas);
        }else {
            try{
                int index = 0;
                dataFile.position(currentFilePos);
                while(dataFile != null && index < num){
                    T data =  dataFile.readObject(dataType);
                    if(data != null){
                        datas.add(data);
                    }else {
                        dataFile = dataFile.next();
                        dataFile.position(currentFilePos);
                        //如果下一个文件是当前文件 那就剩余数据都从当前文件中读
                        if(dataFile.isCurrent() && num - index - 1 > 0){
                            readCurrentLogDataFile(dataFile, 0, num - index - 1, datas);
                            break;
                        }
                    }
                }
            }catch (IOException e){
                logger.error("读取文件失败", e);
                return null;
            }
        }

        return datas;
    }

    @Override
    public T search(String indexField, byte[] indexKey) {
        SeqIndexFile indexFile = indexFileMappings.get(indexField);
        if(indexFile == null){
            throw new RuntimeException(String.format("%s is not index", indexField));
        }
        SeqIndex index = indexFile.readObject(indexKey);
        if(index == null){
            return null;
        }
        SeqDataFile<T> dataFile = dataFiles.get(index.getFileSeq());
        long currentFilePos = index.getDataPos();
        return doSearch(dataFile, currentFilePos);
    }

    /**
     * 读取当前活跃文件
     * @param num
     * @param pos
     * @param dataFile
     * @return
     */
    private void readCurrentLogDataFile(SeqDataFile<T> dataFile, long pos, int num, List<T> datas) {
        readLock.lock();
        try{
            dataFile.position(pos);
            int count = 0;
            while (count < num){
                T data = dataFile.readObject(dataType);
                datas.add(data);
                count++;
            }

        }catch (IOException e){
            logger.error("读取文件失败", e);
        }finally {
            readLock.unlock();
        }
    }

}
