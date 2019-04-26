package com.zq.sword.array.data.storage;

import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 分片系统
 * @author: zhouqi1
 * @create: 2019-01-16 16:39
 **/
public class DataPartitionSystem implements PartitionSystem {

    private static final Map<String, PartitionSystem> PARTITION_SYSTEM = new HashMap<>();

    /**
     * 系统存储主路径
     */
    private String storePath;

    /**
     * 分片Group|Name -> Partition
     */
    private Map<String, Partition> partitionOfGroupNames;

    /**
     * 分片Group->Partition
     */
    private Map<String, List<Partition>> partitionOfGroups;

    /**
     * 已删除的分片
     */
    private List<Partition> deletedPartitions;

    /**
     * 定时线程执行器
     */
    private TimedTaskExecutor executor;

    /**
     * 获取分片系统
     * @param storagePath
     * @return
     */
    public static synchronized PartitionSystem get(String storagePath){
        PartitionSystem partitionSystem = PARTITION_SYSTEM.get(storagePath);
        if(partitionSystem == null){
            partitionSystem = new DataPartitionSystem(storagePath);
        }
        return partitionSystem;
    }

    private DataPartitionSystem(String storePath) {
        this.storePath = storePath;
        this.partitionOfGroupNames = new HashMap<>();
        this.partitionOfGroups = new HashMap<>();
        this.deletedPartitions = new ArrayList<>();
        this.executor = new SingleTimedTaskExecutor();

        //加载已存在的分片数据
        loadResources(storePath);

        //初始化定时任务
        initTimedTasks();
    }

    /**
     * 初始化定时任务
     */
    private void initTimedTasks() {

        //定时清除已删除的分片数据
        executor.timedExecute(()->{
            synchronized (partitionOfGroupNames){
                if(deletedPartitions != null && !deletedPartitions.isEmpty()){
                    List<Partition> needDeletedPartitions = new ArrayList<>();
                    needDeletedPartitions.addAll(deletedPartitions);
                    deletedPartitions.clear();

                    for (Partition partition : needDeletedPartitions){
                        partition.destroy();
                    }
                }
            }
        }, 5, TimeUnit.MINUTES);
    }

    /**
     * 加载已存在的分片数据
     * @param storePath
     */
    private void loadResources(String storePath) {
        File resourceDir = new File(storePath);
        if(!resourceDir.exists()){
            resourceDir.mkdirs();
            return;
        }
        File[] groupDirs = resourceDir.listFiles();
        if(groupDirs != null && groupDirs.length > 0){
            for (File groupDir : groupDirs){
                String group = groupDir.getName();
                File[] partFiles = groupDir.listFiles();
                List<Partition> partitions = new ArrayList<>();
                if(partFiles != null && partFiles.length > 0) {
                    for (File partFile : partFiles) {
                        Partition partition = new DataPartition(this, partFile);
                        String key = getPartitionGroupAndNameKey(group, partition.name());
                        partitionOfGroupNames.put(key, partition);
                        partitions.add(partition);
                    }
                }
                partitionOfGroups.put(group, partitions);
            }
        }
    }

    /**
     * 组合分片的group和name行程key
     * @param group
     * @param name
     * @return
     */
    private String getPartitionGroupAndNameKey(String group, String name){
        return String.format("%s|%s", group, name);
    }

    @Override
    public Collection<Partition> getPartitions() {
        return Collections.unmodifiableCollection(partitionOfGroupNames.values());
    }

    @Override
    public void destroy() {

    }

    @Override
    public String storePath(){
        return storePath;
    }

    @Override
    public boolean exist(String group) {
        return partitionOfGroups.containsKey(group);
    }

    @Override
    public boolean exist(String group, String partName) {
        return partitionOfGroups.containsKey(getPartitionGroupAndNameKey(group, partName));
    }

    @Override
    public boolean exist(Partition partition) {
        return exist(partition.group(), partition.name());
    }

    @Override
    public Partition getPartition(String group, String partName) {
        return partitionOfGroupNames.get(getPartitionGroupAndNameKey(group, partName));
    }

    @Override
    public Partition createPartition(String group, String partName) {
        synchronized (partitionOfGroupNames) {
            if (exist(group, partName)) {
                throw new RuntimeException("this part is exist !");
            }
            Partition partition = new DataPartition(this, group, partName);
            partitionOfGroupNames.put(getPartitionGroupAndNameKey(group, partName), partition);

            List<Partition> partitions = partitionOfGroups.get(group);
            if (partitions == null) {
                partitions = new ArrayList<>();
                partitionOfGroups.put(group, partitions);
            }
            partitions.add(partition);
            return partition;
        }
    }

    @Override
    public Partition getOrNewPartition(String group, String partName) {
        Partition partition = getPartition(group, partName);
        if(partition == null){
            partition = createPartition(group, partName);
        }
        return partition;
    }

    @Override
    public Partition move(Partition partition, String group) {
        return move(partition, group, partition.name());
    }

    @Override
    public Partition move(Partition partition, String group, String name) {
        //复制一个新的分片
        Partition targetPartition = partition.copy(group, name);

        //删除原有分片
        remove(partition);
        return targetPartition;
    }

    @Override
    public Partition copy(Partition partition, String group) {
        return copy(partition, group, partition.name());
    }

    @Override
    public Partition copy(Partition partition, String group, String name) {
        return partition.copy(group, name);
    }

    @Override
    public boolean remove(String group) {
        synchronized (partitionOfGroupNames){
            List<Partition> partitions = partitionOfGroups.get(group);
            if(partitions == null){
                return true;
            }

            for (Partition partition : partitions) {
                String key = getPartitionGroupAndNameKey(group, partition.name());
                partitionOfGroupNames.remove(key);
            }
            deletedPartitions.addAll(partitions);
        }
        return false;
    }

    @Override
    public boolean remove(String group, String partName) {
        synchronized (partitionOfGroupNames){
            String key = getPartitionGroupAndNameKey(group, partName);
            Partition partition = partitionOfGroupNames.get(key);
            if(partition == null){
                return true;
            }
            partitionOfGroupNames.remove(key);
            deletedPartitions.add(partition);
        }
        return true;
    }

    @Override
    public boolean remove(Partition partition) {
        return remove(partition.group(), partition.name());
    }

    @Override
    public Collection<Partition> getPartitions(String group) {
        return partitionOfGroups.containsKey(group) ? Collections.unmodifiableCollection(partitionOfGroups.get(group)) : null;
    }
}
