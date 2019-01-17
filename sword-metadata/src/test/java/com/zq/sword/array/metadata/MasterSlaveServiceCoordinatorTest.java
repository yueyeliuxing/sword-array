package com.zq.sword.array.metadata;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeNamingInfo;
import com.zq.sword.array.metadata.data.NodeType;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import org.junit.Test;

import java.io.IOException;

public class MasterSlaveServiceCoordinatorTest {

    @Test
    public void register() throws IOException {
        //1.zk获取参数
        String connectAddr = "localhost:2181";
        int timeout = 100000;
//        node.id.type=DC_UNIT_PIPER
//        node.id.dc.name=hz
//        node.id.unit.category.name=units
//        node.id.unit.name=unit1
//        node.id.group.name=piperName
        NodeId nodeId = new NodeId(NodeType.DC_UNIT_PIPER, "hz", "units", "unit2", "piperName");
        MetadataCenter metadataCenter = new ZkMatedataCenter(connectAddr, timeout);
        MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = metadataCenter.getMasterSlaveServiceCoordinator(nodeId);
        NodeNamingInfo nodeNamingInfo = new NodeNamingInfo();
        nodeNamingInfo.setHost("127.0.0.1");
        nodeNamingInfo.setPort(8085);
        nodeNamingInfo.setBackupPort(6325);
        masterSlaveServiceCoordinator.register(nodeNamingInfo);
        System.out.println("asdsad==");
        System.in.read();
        //DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);
    }

    @Test
    public void getMasterNodeNamingInfo() {
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
        MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = metadataCenter.getMasterSlaveServiceCoordinator(nodeId);
        NodeNamingInfo nodeNamingInfo = masterSlaveServiceCoordinator.getMasterNodeNamingInfo(new HotspotEventListener<NodeNamingInfo>() {
            @Override
            public void listen(HotspotEvent<NodeNamingInfo> dataEvent) {
                System.out.println(dataEvent);
            }
        });
        System.out.println("asdsad=="+nodeNamingInfo);
    }
}