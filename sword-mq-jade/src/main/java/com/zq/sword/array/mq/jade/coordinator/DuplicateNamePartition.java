package com.zq.sword.array.mq.jade.coordinator;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: sword-array
 * @description: 副本分片
 * @author: zhouqi1
 * @create: 2019-01-17 11:33
 **/
@Data
@ToString
public class DuplicateNamePartition extends NamePartition {

    /**
     * 备份分片
     */
    private List<NamePartition> slaves;

    public DuplicateNamePartition(long id) {
        super(id);
    }

    public DuplicateNamePartition(long id, String topic, String location) {
        super(id, topic, location);
    }
}
