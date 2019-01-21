package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.common.event.*;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.mq.jade.coordinator.data.NamePartition;
import com.zq.sword.array.tasks.Actuator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zq.sword.array.common.event.HotspotEventType.PARTITION_NODE_DEL;

/**
 * @program: sword-array
 * @description: 分片映射器
 * @author: zhouqi1
 * @create: 2019-01-17 14:37
 **/
public class PartitionMapper extends AbstractHotspotEventEmitter implements Actuator, HotspotEventEmitter {

    private NameCoordinator nameCoordinator;

    private String[] topics;

    /**
     * top 分片映射
     */
    private Map<String, List<NameDuplicatePartition>> partitionsOfTopic;

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
                List<NameDuplicatePartition> partitions = new ArrayList<>();
                List<NameDuplicatePartition> duplicatePartitions = nameCoordinator.gainDuplicatePartition(topic, new NamePartitionHotspotEventListener(topic));
                if(duplicatePartitions != null && !duplicatePartitions.isEmpty()){
                    for(NameDuplicatePartition duplicatePartition : duplicatePartitions){
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
    public List<NameDuplicatePartition> findPartition(String topic){
        return partitionsOfTopic.get(topic);
    }

    @Override
    public void stop() {
        partitionsOfTopic.clear();
    }

    /**
     * 分片变动监听器
     */
    private class NamePartitionHotspotEventListener implements HotspotEventListener<List<NameDuplicatePartition>> {

        private String topic;

        public NamePartitionHotspotEventListener(String topic) {
            this.topic = topic;
        }

        @Override
        public void listen(HotspotEvent<List<NameDuplicatePartition>> dataEvent) {
            HotspotEventType type = dataEvent.getType();
            List<NameDuplicatePartition> nameParts = dataEvent.getData();
            List<NameDuplicatePartition> writableParts = partitionsOfTopic.get(topic);
            switch (type){
                case PARTITION_NODE_CHANGE:
                    if(writableParts == null){
                        writableParts = new ArrayList<>();
                        writableParts.addAll(nameParts);
                        return;
                    }
                    writableParts.clear();
                    writableParts.addAll(nameParts);
                    break;
                case PARTITION_NODE_DEL:
                    if(writableParts != null){
                        NameDuplicatePartition namePart = nameParts.get(0);
                        Iterator<NameDuplicatePartition> partitionIterator = writableParts.iterator();
                        while (partitionIterator.hasNext()){
                            NamePartition namePartition = partitionIterator.next();
                            if(namePartition.getId() == namePart.getId()){
                                partitionIterator.remove();
                                emitter(new HotspotEvent(PARTITION_NODE_DEL, namePartition.getId()));
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
