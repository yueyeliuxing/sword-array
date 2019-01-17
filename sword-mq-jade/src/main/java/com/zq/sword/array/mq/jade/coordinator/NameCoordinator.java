package com.zq.sword.array.mq.jade.coordinator;

import com.zq.sword.array.common.event.HotspotEventListener;

import java.util.List;

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
    void registerBroker(NameBroker broker);

    /**
     * partition 注册
     * @param partition 分片
     */
    void registerPartition(NamePartition partition);

    /**
     * 获取指定分片的备份分片
     * @param partition
     * @return
     */
    DuplicateNamePartition gainDuplicatePartition(NamePartition partition, HotspotEventListener<DuplicateNamePartition> partitionEventListener);

    /**
     * 获取备份分片
     * @param topic 主题
     * @param partitionEventListener  监听分片的变动
     * @return 分片集合
     */
    List<DuplicateNamePartition> gainDuplicatePartition(String topic, HotspotEventListener<DuplicateNamePartition> partitionEventListener);

    /**
     * 获取备份分片
     * @param topic 主题
     * @param excludePartitions 排除的分片
     * @param partitionEventListener  监听分片的变动
     * @return 分片集合
     */
    List<DuplicateNamePartition> gainDuplicatePartition(String topic, List<NamePartition> excludePartitions, HotspotEventListener<DuplicateNamePartition> partitionEventListener);


}
