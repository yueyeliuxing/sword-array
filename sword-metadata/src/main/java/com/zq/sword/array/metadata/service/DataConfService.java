package com.zq.sword.array.metadata.service;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.*;
import com.zq.sword.array.common.service.Service;

import java.util.Map;

/**
 * @program: sword-array
 * @description: Zk服务
 * @author: zhouqi1
 * @create: 2018-07-24 10:26
 **/
public interface DataConfService extends Service {


    /**
     * 得到本机房所有单元的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllUnitMetadataInfo(NodeServerId nodeServerId);

    /**
     * 得到本机房所有单元的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> unitsMetadataInfoListener);

    /**
     * 得到本机房代理单元的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllProxyUnitMetadataInfo(NodeServerId nodeServerId);

    /**
     * 得到本机房代理单元的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllProxyUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> proxyUnitsMetadataInfoListener);

    /**
     * 得到本机房其他机房代理的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllOtherProxyUnitMetadataInfo(NodeServerId nodeServerId);

    /**
     * 得到本机房其他机房代理的元数据信息
     * @return
     */
    Map<NodeServerId, NodeMetadataInfo> getAllOtherProxyUnitMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> otherDcProxyMetadataInfoListener);

    /**
     * 获取指定Id的元数据信息
     * @param nodeServerId
     * @return
     */
    NodeMetadataInfo getMetadataInfo(NodeServerId nodeServerId);

    /**
     * 获取指定Id的元数据信息
     * @param nodeServerId
     * @return
     */
    NodeMetadataInfo getMetadataInfo(NodeServerId nodeServerId, DataEventListener<NodeMetadataInfo> metadataInfoListener);


    /**
     * 设置元数据的master信息
     * @param nodeServerId
     * @param nodeServerInfo
     */
    boolean setNodeServerMetadataMasterInfo(NodeServerId nodeServerId, NodeServerInfo nodeServerInfo);

    /**
     * 设置元数据的master信息
     * @param nodeServerId
     * @param nodeConsumptionInfo
     */
    void setNodeServerMetadataConsumeUnitData(NodeServerId nodeServerId, NodeConsumptionInfo nodeConsumptionInfo);
}
