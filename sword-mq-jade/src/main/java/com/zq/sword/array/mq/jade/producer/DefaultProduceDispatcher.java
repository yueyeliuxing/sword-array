package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.broker.RpcPartition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.tasks.SingleTimedTaskExecutor;
import com.zq.sword.array.tasks.TimedTaskExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: broker 生产者调度器
 * @author: zhouqi1
 * @create: 2019-01-17 16:15
 **/
public class DefaultProduceDispatcher implements ProduceDispatcher{

    private static Map<Long, ProduceDispatcher> DISPATCHERS = new HashMap<>();

    protected PartitionMapper partitionMapper;

    private Map<Long, Partition> partitionsOfId;

    private TimedTaskExecutor timedTaskExecutor;

    static{
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            if(!DISPATCHERS.isEmpty()){
                for(ProduceDispatcher produceDispatcher : DISPATCHERS.values()){
                    ((DefaultProduceDispatcher)produceDispatcher).destroy();
                }
            }
        }));
    }

    /**
     * 创建分派器
     * @param connectAddr
     * @return
     */
    public static ProduceDispatcher createDispatcher(String connectAddr){
        return  createDispatcher(new ZkNameCoordinator(connectAddr));

    }

    /**
     * 创建分派器
     * @param coordinator
     * @return
     */
    public static ProduceDispatcher createDispatcher(NameCoordinator coordinator){
        synchronized (DISPATCHERS){
            ProduceDispatcher produceDispatcher = DISPATCHERS.get(coordinator.id());
            if(produceDispatcher == null){
                produceDispatcher = new DefaultProduceDispatcher(coordinator);
                DISPATCHERS.put(coordinator.id(), produceDispatcher);
            }
            return produceDispatcher;
        }
    }

    private DefaultProduceDispatcher(NameCoordinator coordinator) {
        this.partitionMapper = new PartitionMapper(coordinator);
        this.partitionsOfId = new ConcurrentHashMap<>();
        this.timedTaskExecutor = new SingleTimedTaskExecutor();
        //初始化
        init();
    }

    private PartitionAlloter createPartitionAlloter(PartitionSelectStrategy selectStrategy){
        return new PartitionAlloter() {
            @Override
            public PartitionResource allotPartition(String topic) {
                List<NameDuplicatePartition> partitions =  partitionMapper.findPartition(topic);
                return selectStrategy.select(partitions);
            }
        };
    }

    /**
     * 创建一个选择器
     * @return
     */
    private PartitionSelectStrategy createDefaultPartitionSelectStrategy(){
        return new PartitionSelectStrategy(){

            @Override
            public PartitionResource select(List<NameDuplicatePartition> partitions) {
                if(partitions != null && !partitions.isEmpty()){
                    int size = partitions.size();
                    int index = ThreadLocalRandom.current().nextInt(0, size);
                    NameDuplicatePartition namePartition = partitions.get(index);
                    Partition partition = getPartition(namePartition.getId());
                    if(partition == null){
                        partition = new RpcPartition(namePartition.getId(), namePartition.getLocation(), namePartition.getTopic(), namePartition.getTag());
                        addPartition(partition);
                    }
                    return new PartitionResource(partition);
                }
                return null;
            }
        };
    }


    @Override
    public Producer createGeneralProducer() {
        return createGeneralProducer(createDefaultPartitionSelectStrategy());
    }

    @Override
    public Producer createGeneralProducer(PartitionSelectStrategy selectStrategy) {
        return  new GeneralProducer(createPartitionAlloter(selectStrategy));
    }

    public void addPartition(Partition partition){
        partitionsOfId.put(partition.id(), partition);
    }

    public Partition getPartition(long partId){
        return partitionsOfId.get(partId);
    }


    public void init() {
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

    /**
     * 销毁
     */
    public void destroy() {
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
