package com.zq.sword.array.mq.jade.broker;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 容器默认实现
 * @author: zhouqi1
 * @create: 2019-01-16 15:40
 **/
public class DefaultConfigurableContainer implements ConfigurableContainer{

    private long id;

    private Map<Long, Partition> partitionOfIds;

    public DefaultConfigurableContainer(long id) {
        this.id = id;
        partitionOfIds = new ConcurrentHashMap<>();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void put(Long partId, Partition partition) {
        partitionOfIds.put(partId, partition);
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public boolean isEmpty() {
        return partitionOfIds.isEmpty();
    }

    @Override
    public boolean contains(Long partId) {
        return partitionOfIds.containsKey(partId);
    }

    @Override
    public Partition getPartition(Long partId) {
        return partitionOfIds.get(partId);
    }

    @Override
    public Collection<Partition> getPartitions() {
        return partitionOfIds.values();
    }
}
