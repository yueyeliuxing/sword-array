package com.zq.sword.array.mq.jade.coordinator;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.mq.jade.coordinator.data.*;
import com.zq.sword.array.mq.jade.coordinator.util.ZkMqPathBuilder;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.zq.sword.array.common.event.HotspotEventType.*;
import static com.zq.sword.array.mq.jade.coordinator.util.ZkMqPathBuilder.*;

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
    public boolean registerBroker(NameBroker broker) {
        String brokerRegisterPath = ZkMqPathBuilder.buildBrokerRegisterPath(broker);
        if(client.exists(brokerRegisterPath)){
            String data = client.readData(brokerRegisterPath);
            if(data.equals(broker.getLocation())){
                return true;
            }
            return false;
        }
        client.createEphemeral(brokerRegisterPath, broker.getLocation());
        return true;
    }

    @Override
    public boolean registerPartition(NamePartition partition) {
        //创建分片父节点
        createPartitionParentNode(partition.getTopic());

        String partitionRegisterPath = ZkMqPathBuilder.buildPartitionRegisterPath(partition);
        if(client.exists(partitionRegisterPath)){
            String data = client.readData(partitionRegisterPath);
            DuplicatePartitionInfo duplicatePartitionInfo = new DuplicatePartitionInfo();
            duplicatePartitionInfo.deserialize(data.getBytes());
            if(duplicatePartitionInfo.getMaster().equals(partition.getLocation())){
                return true;
            }
            duplicatePartitionInfo.addSlave(String.format("%s|%s", partition.getId(), partition.getLocation()));
            client.writeData(partitionRegisterPath, new String(duplicatePartitionInfo.serialize()));
            return false;
        }
        client.createEphemeral(partitionRegisterPath, new DuplicatePartitionInfo(partition.getLocation()));
        return true;
    }

    /**
     * 创建分片父节点
     * @param topic
     * @return
     */
    private String createPartitionParentNode(String topic) {
        String partitionParentPath = buildPartitionParentPath(topic);
        if(!client.exists(partitionParentPath)){
            client.createPersistent(partitionParentPath, true);
        }
        return partitionParentPath;
    }

    @Override
    public List<NameDuplicatePartition> gainDuplicatePartition(String topic, HotspotEventListener<Map<String, List<NameDuplicatePartition>>> partitionEventListener) {
        List<NameDuplicatePartition> duplicatePartitions = new ArrayList<>();
        //创建分片父节点
        String partitionParentPath = createPartitionParentNode(topic);
        List<String> partIds =  client.getChildren(partitionParentPath);
        //准换节点数据到duplicatePartitions
        assignmentDuplicatePartitions(topic, duplicatePartitions, partIds);
        client.subscribeChildChanges(partitionParentPath, (parentPath, currentChilds)->{
            Map<String, List<NameDuplicatePartition>> duplicatePartMap = new HashMap<>();
            List<NameDuplicatePartition> duplicateParts = new ArrayList<>();
            HotspotEvent< Map<String, List<NameDuplicatePartition>>> event = new HotspotEvent<>();
            event.setType(PARTITION_NODE_CHANGE);
            //准换节点数据到duplicatePartitions
            assignmentDuplicatePartitions(topic, duplicateParts, currentChilds);
            duplicatePartMap.put(topic, duplicateParts);
            event.setData(duplicatePartMap);
            partitionEventListener.listen(event);
        });
        return duplicatePartitions;
    }

    @Override
    public Map<String, List<NameDuplicatePartition>> gainDuplicatePartition(HotspotEventListener<Map<String, List<NameDuplicatePartition>>> partitionEventListener) {
        Map<String, List<NameDuplicatePartition>> duplicatePartitions = new HashMap<>();
        String topicParentPath = buildTopicParentPath();
        if(client.exists(topicParentPath)){
            List<String> topics = client.getChildren(topicParentPath);
            if(topics != null && !topics.isEmpty()){
                for(String topic : topics){
                    List<NameDuplicatePartition> duplicateParts = gainDuplicatePartition(topic, partitionEventListener);
                    duplicatePartitions.put(topic, duplicateParts);
                }
            }
        }
        return duplicatePartitions;
    }

    /**
     *  /准换节点数据到duplicatePartitions
     * @param topic
     * @param duplicatePartitions
     * @param partIds
     */
    private void assignmentDuplicatePartitions(String topic, List<NameDuplicatePartition> duplicatePartitions, List<String> partIds) {
        if(partIds != null && !partIds.isEmpty()){
            for (String partId : partIds){
                NamePartition partition = new NamePartition(Long.parseLong(partId), topic);
                String partitionPath = buildPartitionRegisterPath(partition);
                String data = client.readData(partitionPath);
                DuplicatePartitionInfo duplicatePartitionInfo = new DuplicatePartitionInfo();
                duplicatePartitionInfo.deserialize(data.getBytes());

                NameDuplicatePartition duplicatePartition = new NameDuplicatePartition(Long.parseLong(partId), topic, duplicatePartitionInfo.getMaster());
                Set<String> slaveStrs = duplicatePartitionInfo.getSlaves();
                if(slaveStrs != null && !slaveStrs.isEmpty()){
                    for(String slaveStr : slaveStrs){
                        String[] params = slaveStr.split("\\|");
                        duplicatePartition.addSlave(new NamePartition(Long.parseLong(params[0]), topic, params[1]));
                    }
                }
                duplicatePartitions.add(duplicatePartition);
            }
        }
    }

    @Override
    public boolean registerConsumer(NameConsumer consumer) {
        //创建consume 父节点
        createConsumerParentNode(consumer.getGroup());

        String consumerRegisterPath = buildConsumerRegisterPath(consumer);
        if(!client.exists(consumerRegisterPath)){
            client.createEphemeral(consumerRegisterPath, consumer.getId());
        }
        return true;
    }

    /**
     * 创建消费者父节点
     * @param group
     * @return
     */
    private String createConsumerParentNode(String group) {
        String consumerParentPath = buildConsumerParentPath(group);
        if(!client.exists(consumerParentPath)){
            client.createPersistent(consumerParentPath, true);
        }
        return consumerParentPath;
    }

    @Override
    public List<NameConsumer> gainConsumers(String group, HotspotEventListener<List<NameConsumer>> eventListener) {
        List<NameConsumer> consumers = new ArrayList<>();
        String consumerParentPath = createConsumerParentNode(group);
        List<String> consumerIds =  client.getChildren(consumerParentPath);
        //准换节点数据到consumers
        assignmentConsumers(group, consumers, consumerIds);
        client.subscribeChildChanges(consumerParentPath, (parentPath, currentChilds)->{
            List<NameConsumer> nameConsumers = new ArrayList<>();
            HotspotEvent<List<NameConsumer>> event = new HotspotEvent<>();
            event.setType(CONSUMER_NODE_CHANGE);
            //准换节点数据到consumers
            assignmentConsumers(group, nameConsumers, currentChilds);
            event.setData(nameConsumers);
            eventListener.listen(event);
        });
        return consumers;
    }

    /**
     * 转换赋值consumer数据
     * @param group
     * @param consumers
     * @param consumerIds
     */
    private void assignmentConsumers(String group, List<NameConsumer> consumers, List<String> consumerIds) {
        if(consumerIds != null && !consumerIds.isEmpty()){
            for(String consumerId : consumerIds){
                consumers.add(new NameConsumer(Long.parseLong(consumerId), group));
            }
        }
    }

    @Override
    public List<NameDuplicatePartition> gainConsumeDuplicatePartition(NameConsumer consumer, HotspotEventListener<List<NameDuplicatePartition>> partitionEventListener) {
        List<NameDuplicatePartition> duplicatePartitions = new ArrayList<>();
        long id = consumer.getId();
        String group = consumer.getGroup();
        String[] topics = consumer.getTopics();
        if(topics != null && topics.length > 0){
            for (String topic : topics){
                String consumerDetailedPath = buildConsumerDetailedPath(group, topic);
                if(!client.exists(consumerDetailedPath)){
                    client.createPersistent(consumerDetailedPath, true);
                    continue;
                }

                //获取指定消费者需要消费的分片信息
                String detailed = client.readData(consumerDetailedPath);
                assignmentDuplicatePartitionsByConsumerId(duplicatePartitions, id, topic, detailed);

                client.subscribeDataChanges(consumerDetailedPath, new IZkDataListener(){

                    @Override
                    public void handleDataChange(String dataPath, Object data) throws Exception {
                        List<NameDuplicatePartition> duplicateParts = new ArrayList<>();
                        HotspotEvent<List<NameDuplicatePartition>> event = new HotspotEvent<>();
                        event.setType(CONSUME_PARTITION_DATA_CHANGE);
                        //准换节点数据到duplicatePartitions
                        assignmentDuplicatePartitionsByConsumerId(duplicateParts, id, topic, data.toString());
                        event.setData(duplicateParts);
                        partitionEventListener.listen(event);
                    }

                    @Override
                    public void handleDataDeleted(String dataPath) throws Exception {

                    }
                });

            }
        }
        return duplicatePartitions;
    }

    private void assignmentDuplicatePartitionsByConsumerId(List<NameDuplicatePartition> duplicatePartitions, long id, String topic, String detailed) {
        ConsumeDetailedInfo detailedInfo = new ConsumeDetailedInfo(detailed);
        List<String> partIds = detailedInfo.getPartIds(id);
        if(partIds == null || partIds.isEmpty()){
            return;
        }
        assignmentDuplicatePartitions(topic, duplicatePartitions, partIds);
    }

    @Override
    public Long gainConsumeMsgId(NameConsumer consumer, NamePartition partition) {
        String consumePartitionPath = buildConsumePartitionPath(consumer.getGroup(), partition.getTopic(), partition.getId());
        if(!client.exists(consumePartitionPath)){
            client.createPersistent(consumePartitionPath, true);
            return 0L;
        }
        String msgId = client.readData(consumePartitionPath);
        return Long.parseLong(msgId);
    }

    @Override
    public void editConsumePartitions(String topic, String group, Map<NameConsumer, List<NameDuplicatePartition>> consumerPartitions) {
        String consumerDetailedPath = buildConsumerDetailedPath(group, topic);
        if(!client.exists(consumerDetailedPath)){
            client.createPersistent(consumerDetailedPath, true);
        }
        ConsumeDetailedInfo consumeDetailedInfo = new ConsumeDetailedInfo();
        if(consumerPartitions != null && !consumerPartitions.isEmpty()){
            for (NameConsumer consumer : consumerPartitions.keySet()){
                List<NameDuplicatePartition> partitions = consumerPartitions.get(consumer);
                for(NameDuplicatePartition partition : partitions){
                    consumeDetailedInfo.addDetailedItem(consumer.getId(), String.valueOf(partition.getId()));
                }
            }
        }
    }

    @Override
    public void recordConsumeMsgId(NameConsumer consumer, NamePartition partition, long msgId) {
        String consumePartitionPath = buildConsumePartitionPath(consumer.getGroup(), partition.getTopic(), partition.getId());
        if(!client.exists(consumePartitionPath)){
            client.createPersistent(consumePartitionPath, true);
        }
        client.writeData(consumePartitionPath, msgId);
    }

    @Override
    public boolean registerConsumeAllocator(NameConsumeAllocator consumeAllocator, HotspotEventListener<Long> eventListener) {
        String consumerAllocatorRegisterPath = ZkMqPathBuilder.buildConsumerAllocatorRegisterPath(consumeAllocator);
        if(client.exists(consumerAllocatorRegisterPath)){
            String data = client.readData(consumerAllocatorRegisterPath);
            if(data.equals(consumeAllocator.getId())){
                return true;
            }
            return false;
        }
        client.createEphemeral(consumerAllocatorRegisterPath, consumeAllocator.getId());
        client.subscribeDataChanges(consumerAllocatorRegisterPath, new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                HotspotEvent<Long> event = new HotspotEvent<>();
                event.setType(CONSUME_ALLOCATOR_NODE_DEL);
                event.setData(0L);
                eventListener.listen(event);
            }
        });
        return true;
    }

}
