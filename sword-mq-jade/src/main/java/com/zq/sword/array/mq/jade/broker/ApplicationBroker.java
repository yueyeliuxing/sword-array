package com.zq.sword.array.mq.jade.broker;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 抽象broker
 * @author: zhouqi1
 * @create: 2019-01-16 16:39
 **/
public class ApplicationBroker implements Broker {

    private long id;

    private String resourceLocation;

    private Map<Long, Partition> partitionOfIds;

    public ApplicationBroker(long id, String resourceLocation) {
        this.id = id;
        this.resourceLocation = resourceLocation;
        this.partitionOfIds = new HashMap<>();
        loadResources(resourceLocation);
    }

    private void loadResources(String resourceLocation) {
        File file = new File(resourceLocation);
        if(!file.exists()){
            file.mkdirs();
        }
        File[] topicFiles = file.listFiles();
        if(topicFiles != null && topicFiles.length > 0){
            for (File topicFile : topicFiles){
                File[] partitionFiles = topicFile.listFiles();
                if (partitionFiles != null && partitionFiles.length > 0){
                    for(File partitionFile : partitionFiles){
                        Partition partition = new LocalPartition(this, partitionFile);
                        partitionOfIds.put(partition.id(), partition);
                    }
                }
            }
        }

    }

    @Override
    public Partition newPartition(Long partId) {
        if(contains(partId)){
            return getPartition(partId);
        }
        Partition partition = new LocalPartition(this,partId);
        partitionOfIds.put(partition.id(), partition);
        return partition;
    }

    @Override
    public Partition newPartition(Long partId, String location) {
        if(contains(partId)){
            return getPartition(partId);
        }
        Partition partition = new RpcPartition(this,partId, location);
        partitionOfIds.put(partition.id(), partition);
        return partition;
    }

    @Override
    public long id() {
        return id;
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

    @Override
    public String getResourceLocation(){
        return resourceLocation;
    }

    @Override
    public boolean isEmpty() {
        return partitionOfIds.isEmpty();
    }
}
