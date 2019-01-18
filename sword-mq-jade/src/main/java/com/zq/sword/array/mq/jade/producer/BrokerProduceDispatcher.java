package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.broker.RpcPartition;
import com.zq.sword.array.mq.jade.coordinator.DuplicateNamePartition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.NamePartition;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: broker 生产者调度器
 * @author: zhouqi1
 * @create: 2019-01-17 16:15
 **/
public class BrokerProduceDispatcher extends AbstractProduceDispatcher implements ProduceDispatcher{

    private Broker broker;

    public BrokerProduceDispatcher(NameCoordinator coordinator, Broker broker) {
        super(coordinator);
        this.broker = broker;
    }

    @Override
    protected PartitionSelectStrategy createPartitionSelectStrategy() {
        return new PartitionSelectStrategy() {
            @Override
            public DuplicatePartitionResource select(List<DuplicateNamePartition> partitions) {
                if(partitions != null && !partitions.isEmpty()){
                    for (DuplicateNamePartition partition : partitions){
                        Partition part = broker.getPartition(partition.getId());
                        if( part != null){
                            List<Partition> slavePartitions = new ArrayList<>();
                            List<NamePartition> slaveNamePartitions = partition.getSlaves();
                            if(slaveNamePartitions != null && !slaveNamePartitions.isEmpty()){
                                for(NamePartition slaveNamePartition : slaveNamePartitions){
                                    Partition slavePartition = getPartition(slaveNamePartition.getId());
                                    if(slavePartition == null){
                                        slavePartition = new RpcPartition(slaveNamePartition.getId(), slaveNamePartition.getLocation(), slaveNamePartition.getTopic());
                                        addPartition(slavePartition);
                                    }
                                    slavePartitions.add(slavePartition);
                                }
                            }
                            return new DuplicatePartitionResource(part, slavePartitions);
                        }

                    }
                }
                return null;
            }
        };
    }
}
