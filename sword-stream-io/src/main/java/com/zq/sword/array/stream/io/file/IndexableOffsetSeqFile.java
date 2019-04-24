package com.zq.sword.array.stream.io.file;

/**
 * @program: sword-array
 * @description: 顺序存储
 * @author: zhouqi1
 * @create: 2019-04-18 09:09
 **/
public interface IndexableOffsetSeqFile<T extends IndexableDataWritable> extends OffsetSeqFile<T> {

    /**
     * 通过指定的数据 索引key获取数据
     * @param indexField 索引字段名称
     * @param indexKey 索引字段值
     * @return
     */
    T read(String indexField, byte[] indexKey);
}
