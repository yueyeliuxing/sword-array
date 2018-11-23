package com.zq.sword.array.zpiper.server;

import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.metadata.ConfigManager;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.MasterSlaveServiceCoordinator;
import com.zq.sword.array.metadata.MetadataCenter;
import com.zq.sword.array.metadata.data.*;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import com.zq.sword.array.redis.writer.RedisConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @program: sword-array
 * @description: 服务启动器
 * @author: zhouqi1
 * @create: 2018-10-19 10:15
 **/
@Component
public class ZpiperServerStarter implements CommandLineRunner, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {

        NodeId nodeId = buildNodeId();

        String host = IPUtil.getServerIp();
        int port = getParam("transfer.provider.bind.port", Integer.class);
        int backupPort = getParam("transfer.backup.provider.bind.port", Integer.class);

        //1.zk获取参数
        String connectAddr = getParam("zk.connect.address");
        int timeout = getParam("zk.connect.timeout", Integer.class);
        MetadataCenter metadataCenter = new ZkMatedataCenter(connectAddr, timeout);
        MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = metadataCenter.getMasterSlaveServiceCoordinator(nodeId);
        ConfigManager configManager = metadataCenter.getConfigManager(nodeId);
        DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);

        //判断node 身份是 master 还是slave
        NodeNamingInfo nodeNamingInfo = new NodeNamingInfo(host, port, backupPort);
        NodeInfo nodeInfo = masterSlaveServiceCoordinator.register(nodeNamingInfo);
        NodeRole nodeRole = nodeInfo.getRole();
        if(nodeRole.equals(NodeRole.PIPER_MASTER)){

        }


    }

    private RedisConfig getRedisConfig() {
        String host = getParam("redis.host");
        String port = getParam("redis.port");
        String pass = getParam("redis.pass");
        String timeout = getParam("redis.timeout");
        String maxIdle = getParam("redis.maxIdle");
        String maxTotal = getParam("redis.maxTotal");
        String maxWaitMillis = getParam("redis.maxWaitMillis");
        String testOnBorrow = getParam("redis.testOnBorrow");
        return new RedisConfig(host, port, pass, timeout, maxIdle, maxTotal, maxWaitMillis, testOnBorrow);
    }

    private NodeId buildNodeId() {
        NodeId nodeId = new NodeId();
        nodeId.setType(NodeType.valueOf(getParam("node.id.type")));
        nodeId.setDc(getParam("node.id.dc.name"));
        nodeId.setUnitCategory(getParam("node.id.unit.category.name"));
        nodeId.setUnit(getParam("node.id.unit.name"));
        nodeId.setGroup(getParam("node.id.server.name"));
        return nodeId;
    }

    private String getParam(String key){
        return environment.getProperty(key);
    }
    private <T> T getParam(String key, Class<T> targetClass){
        return environment.getProperty(key, targetClass);
    }

}
