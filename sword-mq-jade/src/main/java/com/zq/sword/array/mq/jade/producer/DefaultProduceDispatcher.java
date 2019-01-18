package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.broker.RpcPartition;
import com.zq.sword.array.mq.jade.coordinator.NameDuplicatePartition;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @program: sword-array
 * @description: broker 生产者调度器
 * @author: zhouqi1
 * @create: 2019-01-17 16:15
 **/
public class DefaultProduceDispatcher extends AbstractProduceDispatcher implements ProduceDispatcher{

    public DefaultProduceDispatcher(String connectAddr, int sessionTimeOut) {
        super(new ZkNameCoordinator(connectAddr, sessionTimeOut));
    }

    @Override
    protected PartitionSelectStrategy createPartitionSelectStrategy() {
        return new PartitionSelectStrategy() {
            @Override
            public PartitionResource select(List<NameDuplicatePartition> partitions) {
                if(partitions != null && !partitions.isEmpty()){
                    int size = partitions.size();
                    int index = ThreadLocalRandom.current().nextInt(0, size-1);
                    NameDuplicatePartition namePartition = partitions.get(index);
                    Partition partition = getPartition(namePartition.getId());
                    if(partition == null){
                        partition = new RpcPartition(namePartition.getId(), namePartition.getLocation(), namePartition.getTopic());
                        addPartition(partition);
                    }
                    return new PartitionResource(partition);
                }
                return null;
            }
        };
    }
}
