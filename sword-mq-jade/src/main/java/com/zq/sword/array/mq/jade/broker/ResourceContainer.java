package com.zq.sword.array.mq.jade.broker;

import java.io.File;
import java.util.Collection;

/**
 * @program: sword-array
 * @description: 资源容器
 * @author: zhouqi1
 * @create: 2019-01-16 15:42
 **/
public class ResourceContainer implements Container{

    private String resourceLocation;

    private ConfigurableContainer container;

    public ResourceContainer(String resourceLocation){
        this.resourceLocation = resourceLocation;
        this.container = new DefaultConfigurableContainer();
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

    public String getResourceLocation(){
        return resourceLocation;
    }
}
