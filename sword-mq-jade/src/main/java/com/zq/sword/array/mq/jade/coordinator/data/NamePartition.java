package com.zq.sword.array.mq.jade.coordinator.data;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * @program: sword-array
 * @description: 分片命名信息
 * @author: zhouqi1
 * @create: 2019-01-17 11:22
 **/
@Getter
@ToString
public class NamePartition {

    /**
     * partition id
     */
    protected Long id;

    /**
     * topic
     */
    protected String topic;

    /**
     * 地址定位：host:port
     */
    protected String location;


    public NamePartition(long id) {
        this.id = id;
    }

    public NamePartition(long id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    public NamePartition(long id, String topic, String location) {
        this.id = id;
        this.topic = topic;
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamePartition partition = (NamePartition) o;
        return id == partition.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
