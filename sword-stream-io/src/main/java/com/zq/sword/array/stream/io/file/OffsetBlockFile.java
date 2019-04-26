package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.stream.io.DataWritable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
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
public class OffsetBlockFile<T extends DataWritable> implements OffsetSeqFile<T> {

    private Logger logger = LoggerFactory.getLogger(OffsetBlockFile.class);

    private static final Map<String, OffsetBlockFile> blockFiles = new HashMap<>();

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 数据类型
     */
    private Class<T> dataType;

    /**
     * 数据文件
     */
    private Map<Long, OffsetFileBlock<T>> dataFiles;

    /**
     * 当前数据文件
     */
    private volatile OffsetFileBlock<T> currentDataFile;

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

    public static synchronized OffsetBlockFile get(String storagePath, Class<?> dataType){
        OffsetBlockFile blockFile = blockFiles.get(storagePath);
        if(blockFile == null){
            blockFile = new OffsetBlockFile(storagePath, dataType);
        }
        return blockFile;
    }

    private OffsetBlockFile(String filePath, Class<T> dataType) {
        this.filePath = filePath;
        this.dataType = dataType;
        this.dataFiles = new ConcurrentHashMap<>();

        //加载数据结构
        initDataFiles();
    }

    /**
     * 加载
     */
    private void initDataFiles() {
        String dataFileDirPath = getFilePath();
        File dataFile = new File(dataFileDirPath);
        if(!dataFile.exists()){
            return;
        }
        File[] dataFiles = dataFile.listFiles();
        if(dataFiles == null){
            return;
        }
        OffsetFileBlock<T> preDataFile = null;
        for (File file : dataFiles){
            OffsetFileBlock<T> seqDataFile = new OffsetFileBlock(file);
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
     * 获取存储路径
     * @return
     */
    @Override
    public String getFilePath(){
        return filePath;
    }

    @Override
    public long write(long offset, T data) {
        try{
            OffsetFileBlock<T> dataFile = searchDataFile(offset);
            long currentFilePos = offset - dataFile.sequence();
            dataFile.position(currentFilePos);
            dataFile.writeObject(data);
        } catch (IOException e) {
            logger.error("写入文件异常", e);
            throw new RuntimeException(e);
        }
        return offset;
    }

    @Override
    public long write(T data) {
        long offset;
        writeLock.lock();
        try{
            //如果当前文件还没有创建 或者 当前文件已写满 需要重新创建
            if(currentDataFile == null || currentDataFile.isFull()){
                OffsetFileBlock<T> preDataFile = currentDataFile;
                currentDataFile = new OffsetFileBlock(getFilePath(), preDataFile == null ? "0" : preDataFile.size()+"");
                dataFiles.put(currentDataFile.sequence(), currentDataFile);
                if(currentDataFile.isFull()){
                    preDataFile.next(currentDataFile);
                }
            }
            long position = currentDataFile.position();
            currentDataFile.writeObject(data);
            offset = position + currentDataFile.sequence();
        }catch (IOException e){
            logger.error("写入文件异常", e);
            throw new RuntimeException(e);
        }finally {
            writeLock.unlock();
        }
        return offset;
    }

    @Override
    public T read(long offset) {
        OffsetFileBlock<T> dataFile = searchDataFile(offset);
        long currentFilePos = offset - dataFile.sequence();
        return doSearch(dataFile, currentFilePos);
    }

    /**
     * 搜索
     * @param dataFile
     * @param currentFilePos
     * @return
     */
    private T doSearch(OffsetFileBlock<T> dataFile, long currentFilePos){
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
    private OffsetFileBlock<T> searchDataFile(long offset) {
        OffsetFileBlock<T> targetDataFile = null;
        for(OffsetFileBlock<T> dataFile : dataFiles.values()){
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
    public List<T> read(long offset, int num) {
        OffsetFileBlock<T> dataFile = searchDataFile(offset);
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
    public void copyTo(OffsetSeqFile<T> targetFile) {
        writeLock.lock();
        try {
            File currentOffsetFileDir = new File(getFilePath());
            File[] dataFiles = currentOffsetFileDir.listFiles();
            if(dataFiles == null){
                return;
            }
            for (File file : dataFiles){
                try(FileChannel inputChannel = new FileInputStream(file).getChannel();
                    FileChannel outputChannel = new FileOutputStream(new File(targetFile.getFilePath() + File.pathSeparator + file.getName())).getChannel();) {
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                } catch (Exception e){
                    logger.error("文件copy错误", e);
                }
            }
        }finally {
            writeLock.unlock();
        }
    }

    /**
     * 读取当前活跃文件
     * @param num
     * @param pos
     * @param dataFile
     * @return
     */
    private void readCurrentLogDataFile(OffsetFileBlock<T> dataFile, long pos, int num, List<T> datas) {
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

    @Override
    public void delete() {
        blockFiles.remove(filePath);
        File file = new File(filePath);
        file.delete();
    }

}
