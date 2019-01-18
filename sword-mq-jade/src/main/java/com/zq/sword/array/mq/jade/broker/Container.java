package com.zq.sword.array.mq.jade.broker;

import java.util.Collection;

/**
 * @program: sword-array
 * @description: 管理分片的容器
 * @author: zhouqi1
 * @create: 2019-01-16 15:31
 **/
public interface Container {

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
    boolean contains(long partId);

    /**
     * 得到指定ID的分片数据
     * @param partId
     * @return
     */
    Partition getPartition(long partId);

    /**
     * 得到所有的分片数据
     * @return
     */
    Collection<Partition> getPartitions();

}
