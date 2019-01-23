package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;

/**
 * @program: sword-array
 * @description: broker
 * @author: zhouqi1
 * @create: 2019-01-17 21:09
 **/
public class ApplicationBroker extends AbstractApplicationBroker implements Broker{

    public ApplicationBroker(long id, String resourceLocation, String zkLocation, String brokerLocation) {
        super(id, resourceLocation, new ZkNameCoordinator(zkLocation), brokerLocation);
    }
}
