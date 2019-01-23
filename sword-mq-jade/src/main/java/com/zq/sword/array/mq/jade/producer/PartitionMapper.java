package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.tasks.Actuator;

import java.util.ArrayList;
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

    /**
     * top 分片映射
     */
    private Map<String, List<NameDuplicatePartition>> partitionsOfTopic;

    /**
     * id -> partitions
     */
    private Map<Long, NameDuplicatePartition> partitionsOfPartId;

    public PartitionMapper(NameCoordinator nameCoordinator) {
        this.nameCoordinator = nameCoordinator;
        this.partitionsOfTopic = new ConcurrentHashMap<>();
        this.partitionsOfPartId = new ConcurrentHashMap<>();
    }

    @Override
    public void start() {
        Map<String, List<NameDuplicatePartition>>  duplicatePartitions = nameCoordinator.gainDuplicatePartition(new NamePartitionHotspotEventListener());
        if(duplicatePartitions != null && !duplicatePartitions.isEmpty()) {
            partitionsOfTopic.putAll(duplicatePartitions);
            transformStructure();
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

    /**
     * 分片ID
     * @param partId
     * @return
     */
    public NameDuplicatePartition findPartition(Long partId){
        return partitionsOfPartId.get(partId);
    }

    @Override
    public void stop() {
        partitionsOfTopic.clear();
    }

    /**
     * 分片变动监听器
     */
    private class NamePartitionHotspotEventListener implements HotspotEventListener<Map<String, List<NameDuplicatePartition>>> {

        @Override
        public void listen(HotspotEvent<Map<String, List<NameDuplicatePartition>>> dataEvent) {
            HotspotEventType type = dataEvent.getType();
            Map<String, List<NameDuplicatePartition>> nameParts = dataEvent.getData();
            switch (type){
                case PARTITION_NODE_CHANGE:
                    for(String topic : nameParts.keySet()){
                        List<NameDuplicatePartition> nameDuplicateParts = nameParts.get(topic);
                        List<NameDuplicatePartition> writableParts = partitionsOfTopic.get(topic);
                        if(writableParts == null){
                            writableParts = new ArrayList<>();
                            partitionsOfTopic.put(topic, writableParts);
                        }
                        writableParts.clear();
                        writableParts.addAll(nameDuplicateParts);
                    }
                    transformStructure();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 结构转换
     */
    private void transformStructure(){
        partitionsOfPartId.clear();
        for (List<NameDuplicatePartition> nameDuplicatePartitions : partitionsOfTopic.values()){
            for(NameDuplicatePartition nameDuplicatePartition : nameDuplicatePartitions){
                partitionsOfPartId.put(nameDuplicatePartition.getId(), nameDuplicatePartition);
            }
        }
    }


}
