package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.ObjectDeserializer;
import com.zq.sword.array.data.ObjectSerializer;
import com.zq.sword.array.data.stream.BitcaskRandomAccessFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

/**
 * @program: sword-array
 * @description: 顺序文件系统
 * @author: zhouqi1
 * @create: 2018-10-31 16:12
 **/
public class BitcaskSequentialFileSystem implements BitcaskFileSystem<SwordData> {

    public static final String FILE_SEPARATOR;

    public static final String FILE_SUFFIX;

    public static final int MAX_FILE_SIZE;

    private String rootDirectory;

    /**
     * 数据序列化器
     */
    private ObjectSerializer<SwordData> swordSerializer;

    /**
     * 数据反序列化器
     */
    private ObjectDeserializer<SwordData> swordDeserializer;

    static {
        FILE_SEPARATOR = System.getProperty("file.separator");
        FILE_SUFFIX = ".data";
        MAX_FILE_SIZE = 100 * 1024 * 1000;
    }

    public BitcaskSequentialFileSystem(String rootDirectory, ObjectSerializer<SwordData> swordSerializer, ObjectDeserializer<SwordData> swordDeserializer) {
        this.rootDirectory = rootDirectory;
        this.swordSerializer = swordSerializer;
        this.swordDeserializer = swordDeserializer;
    }

    @Override
    public String getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public BitcaskRandomAccessFile<SwordData> getDataFile(FileIdGenerater.FileId fileId) throws FileNotFoundException{
        String specifiedFilePath = null;
        String dataDirectoryPath = rootDirectory + FILE_SEPARATOR + fileId.date;
        File dataDirectoryFile = new File(dataDirectoryPath);
        if(!dataDirectoryFile.exists()){
            dataDirectoryFile.mkdirs();
            specifiedFilePath = dataDirectoryPath + FILE_SEPARATOR + fileId.dataId + FILE_SUFFIX;
        }else {
            File[] childFiles =  dataDirectoryFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    long currentFileDataId = Long.parseLong(name.substring(0, name.indexOf(FILE_SUFFIX)));
                    return fileId.dataId >= currentFileDataId;
                }
            });
            if(childFiles != null && childFiles.length > 0){
                File specifiedFile = null;
                long maxDataId = 0;
                for(File file : childFiles){
                    String name = file.getName();
                    long currentFileDataId = Long.parseLong(name.substring(0, name.indexOf(FILE_SUFFIX)));
                    if(currentFileDataId > maxDataId){
                        maxDataId = currentFileDataId;
                        specifiedFile = file;
                    }
                }
                specifiedFilePath = specifiedFile.getAbsolutePath();
                if(specifiedFile.length() >= MAX_FILE_SIZE){
                    specifiedFilePath = dataDirectoryPath + FILE_SEPARATOR + fileId.dataId + FILE_SUFFIX;
                }
            }else {
                specifiedFilePath = dataDirectoryPath + FILE_SEPARATOR + fileId.dataId + FILE_SUFFIX;
            }
        }
        return new BitcaskRandomAccessFile(specifiedFilePath, "rw", swordSerializer, swordDeserializer);
    }
}
