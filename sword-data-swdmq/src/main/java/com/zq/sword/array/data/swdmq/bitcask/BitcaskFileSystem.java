package com.zq.sword.array.data.swdmq.bitcask;

import com.zq.sword.array.data.Sword;
import com.zq.sword.array.data.stream.BitcaskRandomAccessFile;

import java.io.FileNotFoundException;

/**
 * @program: sword-array
 * @description: bitcask文件系统
 * @author: zhouqi1
 * @create: 2018-10-31 15:56
 **/
public interface BitcaskFileSystem<T extends Sword> {

    /**
     * 根目录
     * @return
     */
    String getRootDirectory();

    /**
     * 通过文件ID 获取文件
     * @param fileId
     * @return
     */
    BitcaskRandomAccessFile<T> getDataFile(FileIdGenerater.FileId fileId) throws FileNotFoundException;
}
