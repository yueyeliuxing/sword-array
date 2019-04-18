package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.common.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 日志数据文件
 * @author: zhouqi1
 * @create: 2019-04-17 14:49
 **/
public class SeqDataFile<T extends DataWritable> extends DataFileDecorator implements DataFile {

    private Logger logger = LoggerFactory.getLogger(SeqDataFile.class);

    public static final String OLD_FILE_TYPE_TAG = "old";

    public static final String CURRENT_FILE_TYPE_TAG = "current";

    public static final long FILE_SIZE_MAX_LIMIT = 128 * 1024 * 1024;

    /**
     * 文件序号
     */
    private long sequence;

    /**
     * 是否是当前数据文件
     */
    private boolean isCurrent;

    /**
     * 写一个文件
     */
    private SeqDataFile next;

    public SeqDataFile(File file) {
        super(new OSDataFile(file));
        String[] params = file.getName().split("\\.");
        this.sequence = Long.parseLong(params[0]);
        this.isCurrent = params[1].equalsIgnoreCase(CURRENT_FILE_TYPE_TAG);
    }

    public SeqDataFile(String fileParentPath, String fileName) {
        this(new File(fileParentPath + File.separator + fileName + "." + CURRENT_FILE_TYPE_TAG));
    }

    /**
     * 获取文件编号
     * @return
     */
    public long sequence(){
        return sequence;
    }

    /**
     * 是否是当前文件
     * @return
     */
    public boolean isCurrent(){
        return isCurrent;
    }

    /**
     * 文件是否写满了
     * @return
     */
    public boolean isFull(){
        try {
            return dataFile.size() >= FILE_SIZE_MAX_LIMIT;
        } catch (IOException e) {
            logger.error("文件出现异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 设置下一个数据文件
     * @param dataFile
     */
    public void next(SeqDataFile dataFile){
        next = dataFile;
    }

    /**
     * 返回下一个数据文件
     * @return
     */
    public SeqDataFile next(){
        return next;
    }

    /**
     * 读一个logData
     * @return
     */
    public T readObject(Class<T> clazz){
        T data = ReflectUtils.newInstance(clazz);
        boolean isEnd = false;
        try {
            isEnd = available() <= 0;
            data.read(dataFile);
        } catch (EOFException e) {
            return isEnd ? null : data;
        } catch (IOException e){
            logger.error("错误", e);
        }
        return data;
    }

    /**
     * 读一个logData
     * @return
     */
    public List<T> readObjects(Class<T> clazz, int num){
        List<T> datas = new ArrayList<>(num);
        for(int i = 0; i < num; i++){
            T data = ReflectUtils.newInstance(clazz);
            try{
                data.read(dataFile);
                datas.add(data);
            }catch (EOFException e){
                //读到文件末尾
                break;
            }
        }
        return datas;
    }


    @Override
    public void writeBefore() {
        //如果不是当前文件不可写
        if(!isCurrent){
            throw new RuntimeException("file is not current not write");
        }
    }

    /**
     * 写入data
     * @param data
     */
    public void writeObject(T data){
        writeBefore();
        List<T> datas = new ArrayList<>();
        datas.add(data);
        writeObject(datas);
    }

    /**
     * logs
     * @param datas
     */
    public void writeObject(List<T> datas){
        writeBefore();
        if(datas != null && !datas.isEmpty()){
            for(T data : datas){
                data.write(dataFile);
            }
        }
    }

}
