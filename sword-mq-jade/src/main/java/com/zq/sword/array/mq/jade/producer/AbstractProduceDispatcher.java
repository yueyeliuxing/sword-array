package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 生产调度器
 * @author: zhouqi1
 * @create: 2019-01-17 15:35
 **/
public abstract class AbstractProduceDispatcher implements ProduceDispatcher{

    protected PartitionMapper partitionMapper;

    private PartitionSelectStrategy selectStrategy;

    private Map<Long, Partition> partitionsOfId;

    private TimedTaskExecutor timedTaskExecutor;

    public AbstractProduceDispatcher(NameCoordinator coordinator) {
        this.partitionMapper = new PartitionMapper(coordinator);
        this.selectStrategy = createPartitionSelectStrategy();
        this.partitionsOfId = new ConcurrentHashMap<>();
        this.timedTaskExecutor = new SingleTimedTaskExecutor();
    }

    /**
     * 创建一个选择器
     * @return
     */
    protected abstract PartitionSelectStrategy createPartitionSelectStrategy();

    @Override
    public PartitionResource allotPartition(String topic) {
        List<NameDuplicatePartition> partitions =  partitionMapper.findPartition(topic);
        return selectStrategy.select(partitions);
    }
    @Override
    public Producer createProducer() {
        return new GeneralProducer(this);
    }

    public void addPartition(Partition partition){
        partitionsOfId.put(partition.id(), partition);
    }

    public Partition getPartition(long partId){
        return partitionsOfId.get(partId);
    }

    @Override
    public void start() {
        partitionMapper.start();

        //定时清除已被删除分片的缓存
        timedTaskExecutor.timedExecute(()->{
            Set<Long> delPartIds = new HashSet<>();
            for(Long partId : partitionsOfId.keySet()){
                Partition partition = partitionsOfId.get(partId);
                if(partitionMapper.findPartition(partId) == null){
                    partition.close();
                    delPartIds.add(partId);
                }
            }
            if(!delPartIds.isEmpty()){
                for(Long delPartId : delPartIds){
                    partitionsOfId.remove(delPartId);
                }
            }
        }, 10, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        partitionMapper.stop();
        if(partitionsOfId != null && !partitionsOfId.isEmpty()){
            Collection<Partition> partitions = partitionsOfId.values();
            for (Partition partition : partitions){
                partition.close();
            }
            partitionsOfId.clear();
        }
    }
}
