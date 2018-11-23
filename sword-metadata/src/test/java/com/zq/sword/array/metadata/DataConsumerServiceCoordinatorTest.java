package com.zq.sword.array.metadata;

import com.zq.sword.array.metadata.data.ConsumedDataInfo;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeType;
import com.zq.sword.array.metadata.data.SwordConfig;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DataConsumerServiceCoordinatorTest {

    @Test
    public void getNeedToConsumeNodeNamingInfo() {
    }

    @Test
    public void getConsumedNodeDataInfo() {

        //1.zk获取参数
        String connectAddr = "localhost:2181";
        int timeout = 100000;
//        node.id.type=DC_UNIT_PIPER
//        node.id.dc.name=hz
//        node.id.unit.category.name=units
//        node.id.unit.name=unit1
//        node.id.group.name=piperName
        NodeId nodeId = new NodeId(NodeType.DC_UNIT_PIPER, "hz", "units", "unit1", "piperName");
        MetadataCenter metadataCenter = new ZkMatedataCenter(connectAddr, timeout);
        DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);
       // NodeId coNodeId = new NodeId(NodeType.DC_UNIT_PIPER, "hz", "units", "unit2", "piperName");
       // dataConsumerServiceCoordinator.commitConsumedDataInfo(coNodeId, new ConsumedDataInfo(22222L));
        Map<NodeId, ConsumedDataInfo> consumedDataInfoMap =  dataConsumerServiceCoordinator.getConsumedNodeDataInfo();
        System.out.println(consumedDataInfoMap);
    }

    @Test
    public void commitConsumedDataInfo() {
        //1.zk获取参数
        String connectAddr = "localhost:2181";
        int timeout = 100000;
//        node.id.type=DC_UNIT_PIPER
//        node.id.dc.name=hz
//        node.id.unit.category.name=units
//        node.id.unit.name=unit1
//        node.id.group.name=piperName
        NodeId nodeId = new NodeId(NodeType.DC_UNIT_PIPER, "hz", "units", "unit1", "piperName");
        MetadataCenter metadataCenter = new ZkMatedataCenter(connectAddr, timeout);
        DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);
        NodeId coNodeId = new NodeId(NodeType.DC_UNIT_PIPER, "hz", "units", "unit2", "piperName");
        dataConsumerServiceCoordinator.commitConsumedDataInfo(coNodeId, new ConsumedDataInfo(22222L));
    }
}