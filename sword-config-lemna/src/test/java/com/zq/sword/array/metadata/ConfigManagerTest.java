package com.zq.sword.array.metadata;


import com.zq.sword.array.config.client.ApplicationId;
import com.zq.sword.array.config.client.ArgsConfig;
import com.zq.sword.array.config.client.ZkArgsConfig;
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
        ApplicationId id = new ApplicationId( "hz", "units", "unit1", "piperName");
        ArgsConfig argsConfig = new ZkArgsConfig(connectAddr, id);
        System.out.println(argsConfig.getParam("data.right.queue.file.path"));
    }
}