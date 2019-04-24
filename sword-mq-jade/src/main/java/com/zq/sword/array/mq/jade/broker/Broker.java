package com.zq.sword.array.mq.jade.broker;

import java.util.Collection;

/**
 * @program: sword-array
 * @description: 管理消息分片的容器
 * @author: zhouqi1
 * @create: 2019-01-16 10:27
 **/
public interface Broker {

    /**
     * id
     * @return
     */
    long id();

    /**
     * 是否是空容器
     * @return
     */
    boolean isEmpty();

    /**
     * 包含指定的分片
     * @param partId
     * @return
     */
    boolean contains(Long partId);

    /**
     * 得到指定ID的分片数据
     * @param partId
     * @return
     */
    Partition getPartition(Long partId);

    /**
     * 得到所有的分片数据
     * @return
     */
    Collection<Partition> getPartitions();

    /**
     *  获取资源路径
     * @return
     */
    String getResourceLocation();


    /**
     * 新创建一个分片
     * @param partId
     * @param partId
     * @return
     */
    Partition newPartition(Long partId);

    /**
     * 新创建一个分片
     * @param partId
     * @param partId
     * @return
     */
    Partition newPartition(Long partId, String location);

}
