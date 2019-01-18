package com.zq.sword.array.mq.jade.broker;

import java.io.File;
import java.util.Collection;

/**
 * @program: sword-array
 * @description: 抽象broker
 * @author: zhouqi1
 * @create: 2019-01-16 16:39
 **/
public abstract class AbstractConfigurableBroker implements Broker {

    private String resourceLocation;

    protected ConfigurableContainer container;

    public AbstractConfigurableBroker(long id, String resourceLocation) {
        this.resourceLocation = resourceLocation;
        this.container = createConfigurableContainer(id);
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
            }
        }
    }

    @Override
    public Partition newPartition(String topic, long partId) {
        Partition partition = new MultiPartition(this, topic, partId);
        container.put(partition.id(), partition);
        return partition;
    }

    /**
     * 获取容器
     * @return
     */
    public ConfigurableContainer createConfigurableContainer(long id){
        return new DefaultConfigurableContainer(id);
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

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }
}
