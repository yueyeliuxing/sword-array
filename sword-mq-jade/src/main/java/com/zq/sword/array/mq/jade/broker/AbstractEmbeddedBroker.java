package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.consumer.DefaultConsumeDispatcher;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.mq.jade.coordinator.data.NamePartition;
import com.zq.sword.array.mq.jade.producer.DefaultProduceDispatcher;
import com.zq.sword.array.mq.jade.producer.DuplicatePartitionResource;
import com.zq.sword.array.mq.jade.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @program: sword-array
 * @description: 嵌入式broker 抽象类
 * @author: zhouqi1
 * @create: 2019-01-23 19:10
 **/
public abstract class AbstractEmbeddedBroker extends AbstractApplicationBroker implements EmbeddedBroker{

    private Logger logger = LoggerFactory.getLogger(AbstractEmbeddedBroker.class);

    private String[] topics;

    private String tag;

    public AbstractEmbeddedBroker(long id, String resourceLocation, NameCoordinator coordinator, String brokerLocation) {
        super(id, resourceLocation, coordinator, brokerLocation);
    }

    @Override
    public void topics(String... topics) {
        this.topics = topics;
    }

    @Override
    public void tag(String tag) {
        this.tag = tag;
    }

    @Override
    public Producer createProducer() {
        DefaultProduceDispatcher defaultProduceDispatcher = (DefaultProduceDispatcher)DefaultProduceDispatcher.createDispatcher(coordinator);
        return defaultProduceDispatcher.createGeneralProducer((partitions)->{
                    if(partitions != null && !partitions.isEmpty()){
                        for (NameDuplicatePartition partition : partitions){
                            Partition part = getPartition(partition.getId());
                            if( part != null){
                                List<Partition> slavePartitions = new ArrayList<>();
                                List<NamePartition> slaveNamePartitions = partition.getSlaves();
                                if(slaveNamePartitions != null && !slaveNamePartitions.isEmpty()){
                                    for(NamePartition slaveNamePartition : slaveNamePartitions){
                                        Partition slavePartition = defaultProduceDispatcher.getPartition(slaveNamePartition.getId());
                                        if(slavePartition == null){
                                            slavePartition = new RpcPartition(slaveNamePartition.getId(), slaveNamePartition.getLocation(), slaveNamePartition.getTopic(), slaveNamePartition.getTag());
                                            defaultProduceDispatcher.addPartition(slavePartition);
                                        }
                                        slavePartitions.add(slavePartition);
                                    }
                                }
                                return new DuplicatePartitionResource(part, slavePartitions);
                            }

                        }
                    }
                    return null;
                });
    }

    @Override
    public Consumer createConsumer(String group) {
        return DefaultConsumeDispatcher.createDispatcher(coordinator)
                .createDefaultConsumer(topics, group, (partition)->{
                    logger.info("如果是本地分片就过滤掉, partId->{}", partition.getId());
                    return contains(partition.getId());
                });
    }

    @Override
    public void start() {
        //判断当前topic 是否存在
        if(topics == null){
            throw new NullPointerException("topics is null");
        }

        //初始化指定的topic  生成默认的分片
        for(String topic : topics){
            Collection<Partition> partitions = getPartitions();
            if(partitions == null || partitions.isEmpty()){
                newPartition(topic, tag, generatePartitionId(id(), topic));
            }
        }
        super.start();
    }

    private long generatePartitionId(long id, String topic){
        return Math.abs(String.format("%s-%s", id, topic).hashCode());
    }
}
