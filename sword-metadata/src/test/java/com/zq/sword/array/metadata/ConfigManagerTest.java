package com.zq.sword.array.metadata;


import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.NodeType;
import com.zq.sword.array.metadata.data.SwordConfig;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import org.junit.Test;

public class ConfigManagerTest {

    @Test
    public void config() {
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
        //MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = metadataCenter.getMasterSlaveServiceCoordinator(nodeId);
        ConfigManager configManager = metadataCenter.getConfigManager(nodeId);
        SwordConfig config = configManager.config();
        System.out.println(config.getProperty("data.right.queue.file.path"));
       //DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);
    }
}