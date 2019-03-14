package com.zq.sword.array.mq.jade.coordinator.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 副本分片
 * @author: zhouqi1
 * @create: 2019-01-17 11:33
 **/
@Data
public class NameDuplicatePartition extends NamePartition {

    /**
     * 备份分片
     */
    private List<NamePartition> slaves;


    public NameDuplicatePartition(long id, String topic, String tag, String location) {
        super(id, topic, tag, location);
        this.slaves = new ArrayList<>();
    }

    public void addSlave(NamePartition slave){
        this.slaves.add(slave);
    }

    @Override
    public String toString() {
        return "NameDuplicatePartition{" +
                "slaves=" + slaves +
                ", id=" + id +
                ", topic='" + topic + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
