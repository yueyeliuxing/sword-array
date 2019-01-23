package com.zq.sword.array.mq.jade.coordinator;

import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.mq.jade.coordinator.data.*;

import java.util.List;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 命名协调器
 * @author: zhouqi1
 * @create: 2019-01-17 11:16
 **/
public interface NameCoordinator {

    /**
     * broker 注册
     * @param broker broker容器
     */
    boolean registerBroker(NameBroker broker);

    /**
     * partition 注册
     * @param partition 分片
     */
    boolean registerPartition(NamePartition partition);

    /**
     * 获取备份分片
     * @param topic 主题
     * @param partitionEventListener  监听分片的变动
     * @return 分片集合
     */
    List<NameDuplicatePartition> gainDuplicatePartition(String topic, HotspotEventListener<Map<String, List<NameDuplicatePartition>>> partitionEventListener);

    /**
     * 获取备份分片
     * @param partitionEventListener  监听分片的变动
     * @return 分片集合
     */
    Map<String, List<NameDuplicatePartition>> gainDuplicatePartition(HotspotEventListener<Map<String, List<NameDuplicatePartition>>> partitionEventListener);

    /**
     * 注册消费者
     * @param consumer
     */
    boolean registerConsumer(NameConsumer consumer);

    /**
     * 获取指定group下的所有消费者
     * @param group
     * @param eventListener
     * @return
     */
    List<NameConsumer> gainConsumers(String group, HotspotEventListener<List<NameConsumer>> eventListener);

    /**
     * 获取指定消费者要消费的分片信息
     * @param consumer
     * @param partitionEventListener
     * @return
     */
    List<NameDuplicatePartition> gainConsumeDuplicatePartition(NameConsumer consumer, HotspotEventListener<List<NameDuplicatePartition>> partitionEventListener);

    /**
     * 获区指定消费者消费指定分片的消息ID
     * @param consumer 消费者
     * @param partition 分片
     * @return 指定分片上消费的消息ID
     */
    Long gainConsumeMsgId(NameConsumer consumer, NamePartition partition);

    /**
     * 更改消费者 消费的分片信息
     * @param topic
     * @param group
     * @param consumerPartitions
     */
    void editConsumePartitions(String topic, String group, Map<NameConsumer, List<NameDuplicatePartition>> consumerPartitions);

    /**
     * 记录已经消费的消息ID
     * @param consumer 消费者
     * @param partition 分片
     * @param msgId 消息ID
     */
    void recordConsumeMsgId(NameConsumer consumer, NamePartition partition, long msgId);

    /**
     *
     * @param consumeAllocator
     * @return true 成功 false 失败
     */
    boolean registerConsumeAllocator(NameConsumeAllocator consumeAllocator, HotspotEventListener<Long> eventListener);
}
