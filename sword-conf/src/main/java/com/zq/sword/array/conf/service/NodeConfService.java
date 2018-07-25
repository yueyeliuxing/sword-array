package com.zq.sword.array.conf.service;

import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.node.NodeServerConfig;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.conf.listener.DataEventListener;

/**
 * @program: sword-array
 * @description: NodeServer配置处理
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public interface NodeConfService extends Service {


    /**
     * 注册节点服务配置改变的监听器
     * @param nodeServerId
     * @param dataEventListener
     */
    void registerNodeServerConfigChangeListenter(NodeServerId nodeServerId, DataEventListener dataEventListener);


    /**
     * 获取指定服务ID的服务配置信息
     * @param nodeServerId 服务ID
     * @return 配置信息
     */
    NodeServerConfig getNodeServerConfig(NodeServerId nodeServerId);
}
