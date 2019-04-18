package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.broker.RpcPartition;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameConsumer;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.mq.jade.coordinator.data.NamePartition;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.tasks.Actuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 抽象消费者
 * @author: zhouqi1
 * @create: 2019-01-18 10:39
 **/
public abstract class AbstractConsumer implements Consumer {

    private Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private long id;

    private String group;

    private String[] topics;

    /**
     * 协调器
     */
    private NameCoordinator coordinator;

    /**
     * 消息监听器
     */
    private MessageListener messageListener;

    /**
     * 保存分片消费者
     */
    private Map<Long, PartitionMessageConsumer> messageConsumers;

    /**
     * 消费对应分片的消息ID
     */
    private Map<Long, Long> partitionNextConsumeOffsetMappings;

    private ConsumePartitionFilter partitionFilter;

    public AbstractConsumer(NameCoordinator coordinator, String[] topics, String group) {
        this(coordinator, topics, group, (partition -> false));
    }

    public AbstractConsumer(NameCoordinator coordinator, String[] topics, String group, ConsumePartitionFilter partitionFilter) {
        this(coordinator);
        this.topics = topics;
        this.group = group;
        this.partitionFilter = partitionFilter;
    }



    public AbstractConsumer(NameCoordinator coordinator) {
        this.id = generateConsumerId();
        this.coordinator = coordinator;
        this.messageConsumers = new ConcurrentHashMap<>();
        this.partitionNextConsumeOffsetMappings = new ConcurrentHashMap<>();
    }

    /**
     * 生成消费者ID
     * @return
     */
    protected long generateConsumerId(){
        return new SnowFlakeIdGenerator().nextId();
    }

    @Override
    public void group(String group) {
        this.group = group;
    }

    @Override
    public void listenTopic(String... topics) {
        this.topics = topics;
    }

    @Override
    public void bindingMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * 得到指定分片的消息ID
     * @param partition 分片
     * @return 消息ID
     */
    public long getConsumeOffset(NamePartition partition){
        if(partitionNextConsumeOffsetMappings.containsKey(partition.getId())){
            return partitionNextConsumeOffsetMappings.get(partition.getId());
        }
        Long offset = coordinator.gainConsumeMsgId(new NameConsumer(id, group, topics), partition);
        partitionNextConsumeOffsetMappings.put(partition.getId(), offset);
        return offset;
    }

    @Override
    public void start() {
        NameConsumer consumer = new NameConsumer(id, group, topics);

        //获取可以消费的分片信息
        List<NameDuplicatePartition> duplicateNamePartitions = coordinator.gainConsumeDuplicatePartition(consumer, (dataEvent)->{
            List<NameDuplicatePartition> duplicateNameParts = dataEvent.getData();
            logger.info("消费者消费的分片变动->{}", duplicateNameParts);
            if(duplicateNameParts != null && !duplicateNameParts.isEmpty()){
                HotspotEventType type = dataEvent.getType();
                switch (type){
                    case CONSUME_PARTITION_DATA_CHANGE:
                        Map<Long, PartitionMessageConsumer> tempMessageConsumers = new HashMap<>();
                        for(NameDuplicatePartition duplicateNamePart : duplicateNameParts){
                            Long partId = duplicateNamePart.getId();
                            PartitionMessageConsumer partitionMessageConsumer = messageConsumers.get(partId);
                            if(partitionMessageConsumer != null && !partitionMessageConsumer.isClose()){
                                tempMessageConsumers.put(partId, messageConsumers.get(partId));
                            }else {
                                if(partitionFilter.filter(duplicateNamePart)){
                                    logger.info("分片被过滤->{}", duplicateNamePart);
                                    continue;
                                }
                                Partition partition = createRpcPartition(duplicateNamePart);
                                logger.info("创建分片->{}", partition);
                                if(partition != null){
                                    PartitionMessageConsumer messageConsumer = new PartitionMessageConsumer(partition);
                                    messageConsumer.start();
                                    tempMessageConsumers.put(partition.id(), messageConsumer);
                                    logger.info("对分片—>{}生成消费者", partition.id());
                                }
                            }
                        }
                        for(PartitionMessageConsumer messageConsumer : messageConsumers.values()){
                            messageConsumer.stop();
                        }
                        messageConsumers.clear();
                        messageConsumers.putAll(tempMessageConsumers);
                        break;
                    default:
                        break;
                }
            }
        });

        //对于每个分片创建对应的消费者
        if(duplicateNamePartitions != null && !duplicateNamePartitions.isEmpty()){
            logger.info("消费者消费的分片变动1->{}", duplicateNamePartitions);
            for(NameDuplicatePartition duplicateNamePartition : duplicateNamePartitions){
                if(partitionFilter.filter(duplicateNamePartition)){
                    continue;
                }
                Partition partition = createRpcPartition(duplicateNamePartition);
                logger.info("创建分片1->{}", partition);
                PartitionMessageConsumer messageConsumer = new PartitionMessageConsumer(partition);
                messageConsumer.start();
                messageConsumers.put(partition.id(), messageConsumer);
                logger.info("对分片—>{}生成消费者", duplicateNamePartition.getId());
            }
        }

        logger.info("consumer register 注册：{}", id);
        //注册consumer
        coordinator.registerConsumer(consumer);

    }

    /**
     * 创建分片
     * @param duplicateNamePartition
     * @return
     */
    public Partition createRpcPartition(NameDuplicatePartition duplicateNamePartition){
        return new RpcPartition(duplicateNamePartition.getId(), duplicateNamePartition.getLocation(), duplicateNamePartition.getTopic(), duplicateNamePartition.getTag());
    }

    @Override
    public void stop() {
        for(PartitionMessageConsumer messageConsumer : messageConsumers.values()){
            messageConsumer.stop();
        }
    }

    /**
     * 消息消费者
     */
    private class PartitionMessageConsumer extends AbstractThreadActuator implements Actuator {

        private Logger logger = LoggerFactory.getLogger(PartitionMessageConsumer.class);

        private Partition partition;

        public PartitionMessageConsumer(Partition partition) {
            this.partition = partition;
        }

        @Override
        public void run() {
            try {
                logger.info("消费者开始消费消息");
                Message lastConsumeFailMessage = null;
                while (!isClose && !Thread.currentThread().isInterrupted()) {
                    long offset = getConsumeOffset(new NamePartition(partition.id(), partition.topic(), partition.tag()));
                    Message message = null;
                    if (lastConsumeFailMessage != null) {
                        message = lastConsumeFailMessage;
                    } else {
                        logger.info("消费者消费offset->{}", offset);
                        message = partition.search(offset);
                    }
                    if(message == null){
                        continue;
                    }

                    ConsumeStatus consumeStatus = messageListener.consume(message);
                    switch (consumeStatus) {
                        case CONSUME_SUCCESS:
                            //得到下一个消费消息的偏移量
                            long nextConsumeOffset = offset + message.length();
                            coordinator.recordConsumeOffset(new NameConsumer(id, group, topics), new NamePartition(partition.id(), partition.topic(), partition.tag()), nextConsumeOffset);
                            partitionNextConsumeOffsetMappings.put(partition.id(), nextConsumeOffset);
                            break;
                        case CONSUME_FAIL:
                            lastConsumeFailMessage = message;
                            break;
                        default:
                            break;
                    }
                }
            } finally {
                partition.close();
            }
        }

        public boolean isClose(){
            return partition.isClose();
        }
    }
}
