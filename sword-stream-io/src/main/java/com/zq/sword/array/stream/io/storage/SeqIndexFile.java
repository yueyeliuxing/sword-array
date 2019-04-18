package com.zq.sword.array.stream.io.storage;

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
public class SeqIndexFile extends DataFileDecorator implements DataFile {

    private Logger logger = LoggerFactory.getLogger(SeqIndexFile.class);

    public static final String INDEX_FILE_NAME = ".index";

    private Map<String, SeqIndex> seqIndexCache;

    public SeqIndexFile(File file) {
        super(new OSDataFile(file));
        this.seqIndexCache = new ConcurrentHashMap<>();

        //加载索引数据到内存
        loadIndexsFromDataFile();
    }
    /**
     * 加载索引数据到内存
     */
    private void loadIndexsFromDataFile() {
        try{
            dataFile.position(0);
            while (dataFile.available() > 0){
                SeqIndex seqIndex = new SeqIndex();
                try{
                    seqIndex.read(dataFile);
                    seqIndexCache.putIfAbsent(seqIndex.getKey(), seqIndex);
                }catch (EOFException e){
                    break;
                }
            }
        }catch (IOException e){
            logger.error("打开文件失败", e);
        }
    }

    public SeqIndexFile(String fileParentPath, String fileName) {
        this(new File(fileParentPath + File.separator + fileName + INDEX_FILE_NAME));
    }

    /**
     * 读一个logIndex
     * @return
     */
    public SeqIndex readObject(byte[] key){
        return seqIndexCache.get(new String(key));
    }

    /**
     * 写入log
     * @param index
     */
    public void writeObject(SeqIndex index){
        writeBefore();
        SeqIndex[] logs = {index};
        writeObject(logs);
    }

    /**
     * logs
     * @param logs
     */
    public void writeObject(SeqIndex[] logs){
        writeBefore();
        if(logs != null && logs.length > 0){
            for(SeqIndex log : logs){
                log.write(dataFile);
                seqIndexCache.put(log.getKey(), log);
            }
        }
    }

}
