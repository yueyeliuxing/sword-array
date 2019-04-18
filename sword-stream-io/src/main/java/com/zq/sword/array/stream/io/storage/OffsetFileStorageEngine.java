package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.stream.io.serialize.DataWritable;
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
public class OffsetFileStorageEngine<T extends DataWritable> implements OffsetStorageEngine<T> {

    private Logger logger = LoggerFactory.getLogger(OffsetFileStorageEngine.class);

    /**
     * 数据文件目录名称
     */
    public static final String DATA_FILE_DIR = "data";

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
    private Map<Long, DataBlockFile<T>> dataFiles;

    /**
     * 当前数据文件
     */
    private volatile DataBlockFile<T> currentDataFile;

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

    public OffsetFileStorageEngine(String storagePath, Class<T> dataType) {
        this.storagePath = storagePath;
        this.dataType = dataType;
        this.dataFiles = new ConcurrentHashMap<>();

        //加载数据结构
        initDataFiles();
    }

    /**
     * 加载
     */
    private void initDataFiles() {
        String dataFileDirPath = getDataFileDir();
        File dataFile = new File(dataFileDirPath);
        if(!dataFile.exists()){
            return;
        }
        File[] dataFiles = dataFile.listFiles();
        if(dataFiles == null){
            return;
        }
        DataBlockFile<T> preDataFile = null;
        for (File file : dataFiles){
            DataBlockFile<T> seqDataFile = new DataBlockFile(file);
            this.dataFiles.putIfAbsent(seqDataFile.sequence(), seqDataFile);
            if(preDataFile != null){
                preDataFile.next(seqDataFile);
            }
            preDataFile = seqDataFile;

            if(seqDataFile.isCurrent()){
                currentDataFile = seqDataFile;
                try{
                    currentDataFile.position(currentDataFile.size());
                }catch (IOException e){
                    logger.error("设置文件位置出错", e);
                }
            }
        }
    }

    /**
     * 得到数据文件目录
     * @return
     */
    private String getDataFileDir() {
        return storagePath + File.separator + DATA_FILE_DIR;
    }

    /**
     * 获取存储路径
     * @return
     */
    public String getStoragePath(){
        return storagePath;
    }

    @Override
    public long append(T data) {
        long offset;
        writeLock.lock();
        try{
            //如果当前文件还没有创建 或者 当前文件已写满 需要重新创建
            if(currentDataFile == null || currentDataFile.isFull()){
                DataBlockFile<T> preDataFile = currentDataFile;
                currentDataFile = new DataBlockFile(getDataFileDir(), preDataFile == null ? "0" : preDataFile.size()+"");
                dataFiles.put(currentDataFile.sequence(), currentDataFile);
                if(currentDataFile.isFull()){
                    preDataFile.next(currentDataFile);
                }
            }
            long position = currentDataFile.position();
            currentDataFile.writeObject(data);
            offset = position + currentDataFile.sequence();

            //数据追加到文件后处理工作
            afterAppend(currentDataFile, position, data);
        }catch (IOException e){
            logger.error("写入文件异常", e);
            throw new RuntimeException(e);
        }finally {
            writeLock.unlock();
        }
        return offset;
    }

    /**
     * 数据追加到文件后处理工作
     * @param dataFile 数据写入的文件
     * @param position 在当前文件中的位置
     * @param data
     */
    public void afterAppend(DataBlockFile<T> dataFile, long position, T data){
    }

    @Override
    public T search(long offset) {
        DataBlockFile<T> dataFile = searchDataFile(offset);
        long currentFilePos = offset - dataFile.sequence();
        return doSearch(dataFile, currentFilePos);
    }

    /**
     * 搜索
     * @param dataFile
     * @param currentFilePos
     * @return
     */
    private T doSearch(DataBlockFile<T> dataFile, long currentFilePos){
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
    private DataBlockFile<T> searchDataFile(long offset) {
        DataBlockFile<T> targetDataFile = null;
        for(DataBlockFile<T> dataFile : dataFiles.values()){
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
        DataBlockFile<T> dataFile = searchDataFile(offset);
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

    /**
     * 读取当前活跃文件
     * @param num
     * @param pos
     * @param dataFile
     * @return
     */
    private void readCurrentLogDataFile(DataBlockFile<T> dataFile, long pos, int num, List<T> datas) {
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
