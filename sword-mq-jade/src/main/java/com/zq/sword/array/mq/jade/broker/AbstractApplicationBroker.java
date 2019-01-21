package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.coordinator.data.NameBroker;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NamePartition;

import java.util.Collection;

/**
 * @program: sword-array
 * @description: broker
 * @author: zhouqi1
 * @create: 2019-01-17 20:29
 **/
public abstract class AbstractApplicationBroker extends AbstractConfigurableBroker implements Broker{

    private String brokerLocation;

    private NameCoordinator coordinator;

    public AbstractApplicationBroker(long id, String resourceLocation, NameCoordinator coordinator, String brokerLocation) {
        super(id, resourceLocation);
        this.coordinator = coordinator;
        this.brokerLocation = brokerLocation;
    }

    @Override
    public void start() {

        //注册broker
        coordinator.registerBroker(new NameBroker(id(), brokerLocation));

        //注册分片
        Collection<Partition> partitions = container.getPartitions();
        if(partitions != null && !partitions.isEmpty()){
            for(Partition partition : partitions){
                coordinator.registerPartition(new NamePartition(partition.id(), partition.topic(), brokerLocation));
            }
        }
    }

}
