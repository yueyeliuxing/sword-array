package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zq.sword.array.common.event.HotspotEventType.PARTITION_NODE_DEL;

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

    public AbstractProduceDispatcher(NameCoordinator coordinator) {
        this.partitionMapper = new PartitionMapper(coordinator);
        this.selectStrategy = createPartitionSelectStrategy();
        this.partitionsOfId = new ConcurrentHashMap<>();


        //监听分片删除事件 关闭删除分片 清空缓存
        this.partitionMapper.registerEventListener(dataEvent -> {
            if(dataEvent.getType().equals(PARTITION_NODE_DEL)){
                long partId = (Long) dataEvent.getData();
                Partition partition = partitionsOfId.get(partId);
                if(partition != null){
                    partition.close();
                    partitionsOfId.remove(partId);
                }
            }
        });
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

    @Override
    public void assignTopic(String... topics) {
        this.partitionMapper.topics(topics);
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
