package com.zq.sword.array.mq.jade.broker;

import java.util.Collection;
import java.util.List;
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

    public DefaultConfigurableContainer() {
        partitionOfIds = new ConcurrentHashMap<>();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void put(long partId, Partition partition) {
        partitionOfIds.put(partId, partition);
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public Partition getPartition(long partId) {
        return partitionOfIds.get(partId);
    }

    @Override
    public Collection<Partition> getPartitions() {
        return partitionOfIds.values();
    }
}
