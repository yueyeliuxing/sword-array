package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.mq.jade.coordinator.DuplicateNamePartition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.NamePartition;
import com.zq.sword.array.tasks.Actuator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 分片映射器
 * @author: zhouqi1
 * @create: 2019-01-17 14:37
 **/
public class PartitionMapper implements Actuator {

    private NameCoordinator nameCoordinator;

    private String[] topics;

    /**
     * top 分片映射
     */
    private Map<String, List<DuplicateNamePartition>> partitionsOfTopic;

    public PartitionMapper(NameCoordinator nameCoordinator) {
        this.nameCoordinator = nameCoordinator;
        this.partitionsOfTopic = new ConcurrentHashMap<>();
    }

    public void topics(String... topics){
        this.topics = topics;
    }

    @Override
    public void start() {
        if(topics != null && topics.length > 0){
            for (String topic : topics){
                List<DuplicateNamePartition> partitions = new ArrayList<>();
                List<DuplicateNamePartition> duplicatePartitions = nameCoordinator.gainDuplicatePartition(topic, new NamePartitionHotspotEventListener());
                if(duplicatePartitions != null && !duplicatePartitions.isEmpty()){
                    for(DuplicateNamePartition duplicatePartition : duplicatePartitions){
                        partitions.add(duplicatePartition);
                    }
                }
                partitionsOfTopic.put(topic, partitions);
            }
        }
    }

    /**
     * 查找分片
     * @param topic
     * @return
     */
    public List<DuplicateNamePartition> findPartition(String topic){
        return partitionsOfTopic.get(topic);
    }

    @Override
    public void stop() {
        partitionsOfTopic.clear();
    }

    /**
     * 分片变动监听器
     */
    private class NamePartitionHotspotEventListener implements HotspotEventListener<DuplicateNamePartition> {
        @Override
        public void listen(HotspotEvent<DuplicateNamePartition> dataEvent) {
            HotspotEventType type = dataEvent.getType();
            DuplicateNamePartition namePart = dataEvent.getData();
            String topic = namePart.getTopic();
            List<DuplicateNamePartition> writableParts = partitionsOfTopic.get(topic);
            switch (type){
                case PARTITION_NODE_ADD:
                    if(writableParts == null){
                        writableParts = new ArrayList<>();
                    }
                    writableParts.add(namePart);
                    break;
                case PARTITION_NODE_DEL:
                    if(writableParts != null){
                        Iterator<DuplicateNamePartition> partitionIterator = writableParts.iterator();
                        while (partitionIterator.hasNext()){
                            NamePartition namePartition = partitionIterator.next();
                            if(namePartition.getId() == namePart.getId()){
                                partitionIterator.remove();
                                break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
