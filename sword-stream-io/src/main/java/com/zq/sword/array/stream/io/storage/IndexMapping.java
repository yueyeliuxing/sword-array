package com.zq.sword.array.stream.io.storage;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 索引映射
 * @author: zhouqi1
 * @create: 2019-04-18 10:59
 **/
@Data
@ToString
@NoArgsConstructor
public class IndexMapping {

    /**
     * 索引字段在数据中的偏移量
     */
    private long offset;

    /**
     * 索引字段
     */
    private String indexField;

    public IndexMapping(long offset, String indexField) {
        this.offset = offset;
        this.indexField = indexField;
    }
}
