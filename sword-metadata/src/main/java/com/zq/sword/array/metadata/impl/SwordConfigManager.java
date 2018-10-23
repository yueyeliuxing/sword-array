package com.zq.sword.array.metadata.impl;

import com.zq.sword.array.metadata.ConfigManager;
import com.zq.sword.array.metadata.data.NodeId;
import com.zq.sword.array.metadata.data.SwordConfig;
import com.zq.sword.array.metadata.data.ZkTreePathBuilder;
import org.I0Itec.zkclient.ZkClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 配置管理器
 * @author: zhouqi1
 * @create: 2018-10-22 20:16
 **/
public class SwordConfigManager implements ConfigManager {

    private NodeId nodeId;

    private ZkClient zkClient;

    private static Map<NodeId, ConfigManager> swordConfigManagers;

    static {
        swordConfigManagers = new ConcurrentHashMap<>();
    }

    private SwordConfigManager(NodeId nodeId, ZkClient zkClient) {
        this.nodeId = nodeId;
        this.zkClient = zkClient;
    }

    public synchronized static ConfigManager buildConfigManager(NodeId nodeId, ZkClient zkClient){
        ConfigManager configManager = swordConfigManagers.get(nodeId);
        if(configManager ==  null){
            configManager = new SwordConfigManager(nodeId, zkClient);
            swordConfigManagers.put(nodeId, configManager);
        }
        return configManager;
    }

    @Override
    public SwordConfig config() {
        String nodeConfigPath = ZkTreePathBuilder.buildNodeConfigPath(nodeId);
        String nodeConfigString = zkClient.readData(nodeConfigPath);
        return new SwordConfig(nodeConfigString);
    }
}
