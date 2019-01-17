package com.zq.sword.array.mq.jade.broker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: 抽象broker
 * @author: zhouqi1
 * @create: 2019-01-16 16:39
 **/
public abstract class AbstractConfigurableBroker implements Broker {

    private String resourceLocation;

    private ConfigurableContainer container;

    private Map<String, List<Long>> partIdOfTopics;

    public AbstractConfigurableBroker(String resourceLocation) {
        this.resourceLocation = resourceLocation;
        this.container = new DefaultConfigurableContainer();
        this.partIdOfTopics = new ConcurrentHashMap<>();
        loadResources(resourceLocation);
    }

    private void loadResources(String resourceLocation) {
        File file = new File(resourceLocation);
        if(!file.isDirectory()){
            throw new IllegalArgumentException(String.format("resourceLocation:%s is not a directory"));
        }
        File[] partitionFiles = file.listFiles();
        if (partitionFiles != null && partitionFiles.length > 0){
            for(File partitionFile : partitionFiles){
                Partition partition = new MultiPartition(this, partitionFile);
                container.put(partition.id(), partition);
                List<Long> partIds = partIdOfTopics.get(partition.topic());
                if(partIds == null){
                    partIds = new CopyOnWriteArrayList<>();
                    partIdOfTopics.put(partition.topic(), partIds);
                }
                partIds.add(partition.id());

            }
        }
    }

    @Override
    public Partition newPartition(String topic, long partId) {
        Partition partition = new MultiPartition(this, topic, partId);
        container.put(partition.id(), partition);
        return partition;
    }

    @Override
    public List<Partition> getPartition(String topic) {
        List<Partition> partitions = new ArrayList<>();
        List<Long> partIds = partIdOfTopics.get(topic);
        if(partIds != null && !partIdOfTopics.isEmpty()){
            for(Long partId : partIds){
                partitions.add(getPartition(partId));
            }
        }
        return partitions;
    }

    @Override
    public long id() {
        return container.id();
    }

    @Override
    public Partition getPartition(long partId) {
        return container.getPartition(partId);
    }

    @Override
    public Collection<Partition> getPartitions() {
        return container.getPartitions();
    }

    @Override
    public String getResourceLocation(){
        return resourceLocation;
    }
}
