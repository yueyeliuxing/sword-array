package com.zq.sword.array.mq.jade.coordinator;

import com.zq.sword.array.common.event.HotspotEventListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    public ZkNameCoordinator(String connectAddr, int sessionTimeOut) {
        logger.info("ZkNameCoordinator module starting...");
        client = new ZkClient(new ZkConnection(connectAddr), sessionTimeOut, new ZkSerializer() {
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
    public DuplicateNamePartition gainDuplicatePartition(NamePartition partition, HotspotEventListener<DuplicateNamePartition> partitionEventListener) {
        return null;
    }

    @Override
    public List<DuplicateNamePartition> gainDuplicatePartition(String topic, HotspotEventListener<DuplicateNamePartition> partitionEventListener) {
        return null;
    }

    @Override
    public List<DuplicateNamePartition> gainDuplicatePartition(String topic, List<NamePartition> excludePartitions, HotspotEventListener<DuplicateNamePartition> partitionEventListener) {
        return null;
    }

}
