package com.zq.sword.array.conf.service;

import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.node.NodeServerInfo;
import com.zq.sword.array.conf.listener.DataEventListener;

import java.util.List;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public interface NamingConfService extends Service {

    /**
     * 注册Master节点改变的监听器
     * @param nodeServerId
     * @param dataEventListener
     */
    void registerMasterNodeServerChangeListener(NodeServerId nodeServerId, DataEventListener dataEventListener);

    /**
     * 注册节点服务配置改变的监听器
     * @param nodeServerId
     * @param dataEventListener
     */
    void registerOtherMasterNodeServerInfoChangeListener(NodeServerId nodeServerId, DataEventListener dataEventListener);


    /**
     * 注册指定服务ID的IP port 的数据信息
     * @param nodeServerInfo IP port 的数据信息
     */
    void registerNodeServerInfo(NodeServerInfo nodeServerInfo);

    /**
     * 获取指定master的注册信息
     * @param nodeServerId
     * @return
     */
    NodeServerInfo getMasterNodeServerInfo(NodeServerId nodeServerId);


    /**
     * 获取其他PiperMaster服务的IP port 的数据信息
     * @return IP port 信息
     */
    List<NodeServerInfo> listOtherMasterNodeServerInfo(NodeServerId nodeServerId);
}
