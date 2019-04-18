package com.zq.sword.array.stream.io.storage;

import com.zq.sword.array.stream.io.serialize.DataWritable;

import java.util.List;

/**
 * @program: sword-array
 * @description: 顺序存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:09
 **/
public interface SequentialStorage<T extends DataWritable> {

    /**
     * 追加数据
     * @param data 数据
     * @return 数据所在的偏移量
     */
    long append(T data);

    /**
     * 通过偏移量搜索指定的数据
     * @param offset 偏移量
     * @return 数据
     */
    T search(long offset);

    /**
     * 通过指定偏移量顺序搜索指定数量的数据集合
     * @param offset 偏移量
     * @param num 数量
     * @return 数据集合
     */
    List<T> search(long offset, int num);

    /**
     * 通过指定的数据 索引key获取数据
     * @param indexField 索引字段名称
     * @param indexKey 索引字段值
     * @return
     */
    T search(String indexField, byte[] indexKey);
}
