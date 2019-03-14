package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.network.rpc.server.RpcServer;

/**
 * @program: sword-array
 * @description: 管理消息分片的容器
 * @author: zhouqi1
 * @create: 2019-01-16 10:27
 **/
public interface Broker extends RpcServer, Container {

    /**
     *  获取资源路径
     * @return
     */
    String getResourceLocation();


    /**
     * 新创建一个分片
     * @param topic
     * @param tag
     * @param partId
     * @return
     */
    Partition newPartition(String topic, String tag, long partId);

}
