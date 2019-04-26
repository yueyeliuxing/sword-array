package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.stream.io.DataWritable;

import java.util.List;

/**
 * @program: sword-array
 * @description: 顺序存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:09
 **/
public interface OffsetSeqFile<T extends DataWritable> {

    /**
     * 得到存储路径
     * @return
     */
    String getFilePath();

    /**
     * 末尾 追加数据
     * @param offset 指定文件偏移量
     * @param data 数据
     * @return 数据所在的偏移量
     */
    long write(long offset, T data);

    /**
     * 末尾 追加数据
     * @param data 数据
     * @return 数据所在的偏移量
     */
    long write(T data);

    /**
     * 通过偏移量搜索指定的数据
     * @param offset 偏移量
     * @return 数据
     */
    T read(long offset);

    /**
     * 通过指定偏移量顺序搜索指定数量的数据集合
     * @param offset 偏移量
     * @param num 数量
     * @return 数据集合
     */
    List<T> read(long offset, int num);

    /**
     * 拷贝到另一个文件中
     * @param file
     */
    void copyTo(OffsetSeqFile<T> file);

    /***
     * 删除
     */
    void delete();

}
