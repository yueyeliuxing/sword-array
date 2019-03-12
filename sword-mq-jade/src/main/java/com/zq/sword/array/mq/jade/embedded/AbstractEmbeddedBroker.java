package com.zq.sword.array.mq.jade.embedded;

import com.zq.sword.array.mq.jade.broker.AbstractApplicationBroker;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.network.rpc.server.NettyRpcServer;

import java.util.Collection;

/**
 * @program: sword-array
 * @description: 嵌入式broker 抽象类
 * @author: zhouqi1
 * @create: 2019-01-23 19:10
 **/
public abstract class AbstractEmbeddedBroker extends AbstractApplicationBroker implements EmbeddedBroker{

    private String[] topics;

    private EmbeddedProduceDispatcher produceDispatcher;

    private EmbeddedConsumeDispatcher consumeDispatcher;

    public AbstractEmbeddedBroker(long id, String resourceLocation, NameCoordinator coordinator, String brokerLocation) {
        super(id, resourceLocation, coordinator, brokerLocation);
        this.produceDispatcher = new EmbeddedProduceDispatcher(coordinator, this);
        this.consumeDispatcher = new EmbeddedConsumeDispatcher(coordinator, this);
    }

    protected NettyRpcServer getRpcServer() {
        return rpcServer;
    }

    @Override
    public void topics(String... topics) {
        this.topics = topics;
    }

    @Override
    public Producer createProducer() {
        return produceDispatcher.createProducer();
    }

    @Override
    public Consumer createConsumer(String group) {
        return consumeDispatcher.createConsumer(topics, group);
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
                newPartition(topic, generatePartitionId(id(), topic));
            }
        }
        super.start();
        produceDispatcher.start();
        consumeDispatcher.start();
    }

    private long generatePartitionId(long id, String topic){
        return Math.abs(String.format("%s-%s", id, topic).hashCode());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        produceDispatcher.stop();
        consumeDispatcher.stop();
    }
}
