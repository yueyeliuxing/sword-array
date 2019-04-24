package com.zq.sword.array.stream.io.file;

import com.zq.sword.array.stream.io.DataWritable;

/**
 * @program: sword-array
 * @description: 可索引的数据结构
 * @author: zhouqi1
 * @create: 2019-04-18 10:57
 **/
public interface IndexableDataWritable extends DataWritable {

    /**
     * 索引字段映射表
     * @return
     */
    String[] indexMappings();
}
