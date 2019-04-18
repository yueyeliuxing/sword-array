package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.stream.io.serialize.AbstractRWStore;
import com.zq.sword.array.stream.io.serialize.RWStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 日志数据文件
 * @author: zhouqi1
 * @create: 2019-04-17 14:49
 **/
public class DataIndexFile extends AbstractRWStore implements RWStore {

    private Logger logger = LoggerFactory.getLogger(DataIndexFile.class);

    private File file;

    public static final String INDEX_FILE_NAME = ".index";

    private Map<String, DataIndex> dataIndexCache;

    public DataIndexFile(File file) {
        super(new OSDataFile(file));
        this.file = file;
        this.dataIndexCache = new ConcurrentHashMap<>();

        //加载索引数据到内存
        loadIndexsFromDataFile();
    }
    /**
     * 加载索引数据到内存
     */
    private void loadIndexsFromDataFile() {
        try{
            rwStore.position(0);
            while (rwStore.available() > 0){
                DataIndex seqIndex = new DataIndex();
                try{
                    seqIndex.read(rwStore);
                    dataIndexCache.putIfAbsent(seqIndex.getKey(), seqIndex);
                }catch (EOFException e){
                    break;
                }
            }
        }catch (IOException e){
            logger.error("打开文件失败", e);
        }
    }

    public DataIndexFile(String fileParentPath, String fileName) {
        this(new File(fileParentPath + File.separator + fileName + INDEX_FILE_NAME));
    }

    /**
     * 索引字段
     * @return
     */
    public String indexField(){
        return file.getName().split("\\.")[0];
    }

    /**
     * 读一个logIndex
     * @return
     */
    public DataIndex readObject(byte[] key){
        return dataIndexCache.get(new String(key));
    }

    /**
     * 写入log
     * @param index
     */
    public void writeObject(DataIndex index){
        writeBefore();
        DataIndex[] logs = {index};
        writeObject(logs);
    }

    /**
     * logs
     * @param logs
     */
    public void writeObject(DataIndex[] logs){
        writeBefore();
        if(logs != null && logs.length > 0){
            for(DataIndex log : logs){
                log.write(rwStore);
                dataIndexCache.put(log.getKey(), log);
            }
        }
    }

}
