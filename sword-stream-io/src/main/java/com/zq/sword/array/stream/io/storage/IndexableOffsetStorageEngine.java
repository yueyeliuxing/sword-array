package com.zq.sword.array.stream.io.storage;

/**
 * @program: sword-array
 * @description: 顺序存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:09
 **/
public interface IndexableOffsetStorageEngine<T extends IndexableDataWritable> extends OffsetStorageEngine<T> {

    /**
     * 通过指定的数据 索引key获取数据
     * @param indexField 索引字段名称
     * @param indexKey 索引字段值
     * @return
     */
    T search(String indexField, byte[] indexKey);
}
