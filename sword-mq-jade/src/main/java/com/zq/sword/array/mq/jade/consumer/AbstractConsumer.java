package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.broker.RpcPartition;
import com.zq.sword.array.mq.jade.coordinator.DuplicateNamePartition;
import com.zq.sword.array.mq.jade.coordinator.NameConsumer;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.NamePartition;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.tasks.Actuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private Map<Long, Long> consumeMsgIds;

    public AbstractConsumer(NameCoordinator coordinator) {
        this.id = generateConsumerId();
        this.coordinator = coordinator;
        this.messageConsumers = new ConcurrentHashMap<>();
        this.consumeMsgIds = new ConcurrentHashMap<>();
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
    public long getConsumeMsgId(NamePartition partition){
        if(consumeMsgIds.containsKey(partition.getId())){
            return consumeMsgIds.get(partition.getId());
        }
        Long msgId = coordinator.gainConsumeMsgId(new NameConsumer(id, group, topics), partition);
        consumeMsgIds.put(partition.getId(), msgId);
        return msgId;
    }

    @Override
    public void start() {
        NameConsumer consumer = new NameConsumer(id, group, topics);

        //注册consumer
        coordinator.registerConsumer(consumer);

        //获取可以消费的分片信息
        List<DuplicateNamePartition> duplicateNamePartitions = coordinator.gainConsumeDuplicatePartition(consumer, (dataEvent)->{
            List<DuplicateNamePartition> duplicateNameParts = dataEvent.getData();
            if(duplicateNameParts != null && !duplicateNameParts.isEmpty()){
                HotspotEventType type = dataEvent.getType();
                switch (type){
                    case CONSUME_PARTITION_DATA_CHANGE:
                        Map<Long, PartitionMessageConsumer> tempMessageConsumers = new HashMap<>();
                        for(DuplicateNamePartition duplicateNamePart : duplicateNameParts){
                            long partId = duplicateNamePart.getId();
                            if(messageConsumers.containsKey(partId)){
                                tempMessageConsumers.put(partId, messageConsumers.get(partId));
                                messageConsumers.remove(partId);
                            }else {
                                Partition partition = createRpcPartition(duplicateNamePart);
                                if(partition != null){
                                    PartitionMessageConsumer messageConsumer = new PartitionMessageConsumer(partition);
                                    messageConsumer.start();
                                    messageConsumers.put(partition.id(), messageConsumer);
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
            for(DuplicateNamePartition duplicateNamePartition : duplicateNamePartitions){
                Partition partition = createRpcPartition(duplicateNamePartition);
                if(partition != null){
                    PartitionMessageConsumer messageConsumer = new PartitionMessageConsumer(partition);
                    messageConsumer.start();
                    messageConsumers.put(partition.id(), messageConsumer);
                }
            }
        }

    }

    /**
     * 创建分片
     * @param duplicateNamePartition
     * @return
     */
    protected Partition createRpcPartition(DuplicateNamePartition duplicateNamePartition){
        return new RpcPartition(duplicateNamePartition.getId(), duplicateNamePartition.getTopic(), duplicateNamePartition.getLocation());
    }

    @Override
    public void stop() {

    }

    /**
     * 消息消费者
     */
    private class PartitionMessageConsumer implements Actuator {

        private Logger logger = LoggerFactory.getLogger(PartitionMessageConsumer.class);

        private Thread thread;

        private Partition partition;

        private volatile boolean isClose = false;

        public PartitionMessageConsumer(Partition partition) {
            this.partition = partition;
        }

        @Override
        public void start() {
            thread = new Thread(()->{
                ObjectInputStream inputStream = null;
                try {
                    Message lastConsumeFailMessage = null;
                    inputStream = partition.openInputStream();
                    while (!isClose && !Thread.currentThread().isInterrupted()){
                        Message message = null;
                        if(lastConsumeFailMessage != null){
                            message = lastConsumeFailMessage;
                        }else {
                            long msgId = getConsumeMsgId(new NamePartition(partition.id()));
                            inputStream.skip(msgId);
                            message = (Message) inputStream.readObject();
                        }

                        ConsumeStatus consumeStatus = messageListener.consume(message);
                        switch (consumeStatus){
                            case CONSUME_SUCCESS:
                                coordinator.recordConsumeMsgId(new NameConsumer(id, group, topics), new NamePartition(partition.id()), message.getMsgId());
                                break;
                            case CONSUME_FAIL:
                                lastConsumeFailMessage = message;
                                break;
                            default:
                                break;
                        }
                    }
                } catch (InputStreamOpenException e) {
                    logger.error("打开分片输入流失败", e);
                } catch (IOException e) {
                    logger.error("读取数据出现异常", e);
                } finally {
                    try {
                        inputStream.close();
                        partition.close();
                    } catch (IOException e) {
                        logger.error("关闭输入流失败", e);
                    }
                }

            });
            thread.start();
        }

        @Override
        public void stop() {
            isClose = true;
        }
    }
}
