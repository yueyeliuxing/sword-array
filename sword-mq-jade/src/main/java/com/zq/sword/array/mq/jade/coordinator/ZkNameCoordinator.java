package com.zq.sword.array.mq.jade.coordinator;

import com.zq.sword.array.common.event.HotspotEventListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: zk 协调
 * @author: zhouqi1
 * @create: 2019-01-17 21:12
 **/
public class ZkNameCoordinator implements NameCoordinator {

    private Logger logger = LoggerFactory.getLogger(ZkNameCoordinator.class);

    private ZkClient client;

    public ZkNameCoordinator(ZkClient client) {
        this.client = client;
    }

    public ZkNameCoordinator(String connectAddr) {
        logger.info("ZkNameCoordinator module starting...");
        client = new ZkClient(new ZkConnection(connectAddr), 500, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return (data.toString()).getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });
    }

    @Override
    public void registerBroker(NameBroker broker) {

    }

    @Override
    public void registerPartition(NamePartition partition) {

    }

    @Override
    public NameDuplicatePartition gainDuplicatePartition(NamePartition partition, HotspotEventListener<NameDuplicatePartition> partitionEventListener) {
        return null;
    }

    @Override
    public List<NameDuplicatePartition> gainDuplicatePartition(String topic, HotspotEventListener<List<NameDuplicatePartition>> partitionEventListener) {
        return null;
    }

    @Override
    public List<NameDuplicatePartition> gainDuplicatePartition(String topic, List<NamePartition> excludePartitions, HotspotEventListener<NameDuplicatePartition> partitionEventListener) {
        return null;
    }

    @Override
    public void registerConsumer(NameConsumer consumer) {

    }

    @Override
    public List<NameConsumer> gainConsumers(String topic, String group, HotspotEventListener<List<NameConsumer>> eventListener) {
        return null;
    }

    @Override
    public List<NameDuplicatePartition> gainConsumeDuplicatePartition(NameConsumer consumer, HotspotEventListener<List<NameDuplicatePartition>> partitionEventListener) {
        return null;
    }

    @Override
    public Long gainConsumeMsgId(NameConsumer consumer, NamePartition partition) {
        return null;
    }

    @Override
    public void editConsumePartitions(String topic, String group, Map<NameConsumer, List<NameDuplicatePartition>> consumerPartitions) {

    }

    @Override
    public void recordConsumeMsgId(NameConsumer consumer, NamePartition partition, long msgId) {

    }

    @Override
    public boolean registerConsumeAllocator(NameConsumeAllocator consumeAllocator, HotspotEventListener<Long> eventListener) {
        return false;
    }

}
