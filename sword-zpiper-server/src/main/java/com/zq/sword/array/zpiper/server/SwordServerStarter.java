package com.zq.sword.array.zpiper.server;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;
import com.zq.sword.array.data.bridge.SwordCommandCycleDisposeBridge;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.bitcask.BitcaskLeftOrderlyQueue;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.data.rqueue.bitcask.BitcaskRightRandomQueue;
import com.zq.sword.array.metadata.ConfigManager;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.MasterSlaveServiceCoordinator;
import com.zq.sword.array.metadata.MetadataCenter;
import com.zq.sword.array.metadata.data.*;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import com.zq.sword.array.redis.replicator.SwordSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.writer.RedisCommandWriter;
import com.zq.sword.array.redis.writer.RedisConfig;
import com.zq.sword.array.redis.writer.SwordRedisCommandWriter;
import com.zq.sword.array.transfer.backup.gather.DataTransferBackupGather;
import com.zq.sword.array.transfer.backup.gather.SwordDataTransferBackupGather;
import com.zq.sword.array.transfer.backup.provider.DataTransferBackupProvider;
import com.zq.sword.array.transfer.backup.provider.SwordDataTransferBackupProvider;
import com.zq.sword.array.transfer.client.DataTransferGather;
import com.zq.sword.array.transfer.client.SwordDataTransferGather;
import com.zq.sword.array.transfer.provider.DataTransferProvider;
import com.zq.sword.array.transfer.provider.SwordDataTransferProvider;
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
public class SwordServerStarter implements CommandLineRunner, EnvironmentAware {

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

        //初始化data-birdge
        DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge = new SwordCommandCycleDisposeBridge();

        //初始化R-queue
        String rightDataFilePath = getParam("data.right.queue.file.path");
        String rightIndexFilePath = getParam("data.right.queue.index.file.path");
        RightRandomQueue rightRandomQueue = new BitcaskRightRandomQueue(rightDataFilePath, rightIndexFilePath);
        rightRandomQueue.bindingDataCycleDisposeBridge(dataCycleDisposeBridge);

        //初始化L-Queue
        String leftDataFilePath = getParam("data.left.queue.file.path");
        LeftOrderlyQueue<SwordData> leftOrderlyQueue = new BitcaskLeftOrderlyQueue(leftDataFilePath);
        leftOrderlyQueue.bindingDataCycleDisposeBridge(dataCycleDisposeBridge);

        //判断node 身份是 master 还是slave
        NodeNamingInfo nodeNamingInfo = new NodeNamingInfo(host, port, backupPort);
        NodeInfo nodeInfo = masterSlaveServiceCoordinator.register(nodeNamingInfo);
        NodeRole nodeRole = nodeInfo.getRole();
        if(nodeRole.equals(NodeRole.PIPER_MASTER)){
            //初始化redis slave replicator
            long workId = getParam("redis.replicator.work.id", Long.class);
            long datacenterId = getParam("redis.replicator.datacenter.id", Long.class);
            String redisUri = getParam("redis.replicator.redis.uri");
            SlaveRedisReplicator slaveRedisReplicator = SwordSlaveRedisReplicator.SwordSlaveRedisReplicatorBuilder.create()
                    .idGenerat(workId, datacenterId)
                    .bindingDataSource(rightRandomQueue)
                    .listen(redisUri)
                    .build();
            slaveRedisReplicator.start();

            //初始化 数据传输提供者
            DataTransferProvider dataTransferProvider = SwordDataTransferProvider.SwordDataTransferProviderBuilder.create()
                    .bindingDataSource(rightRandomQueue)
                    .bind(port)
                    .build();
            dataTransferProvider.start();

            //初始化 数据传输收集器
            DataTransferGather dataTransferGather = SwordDataTransferGather.SwordDataTransferGatherBuilder.create()
                    .bindingTargetDataSource(leftOrderlyQueue)
                    .bindingDataConsumerServiceCoordinator(dataConsumerServiceCoordinator)
                    .build();
            dataTransferGather.start();

            //初始化 redis 写入者
            RedisCommandWriter redisCommandWriter = SwordRedisCommandWriter.SwordRedisCommandWriterBuilder.create()
                    .config(getRedisConfig())
                    .bindingDataSource(leftOrderlyQueue)
                    .build();
            redisCommandWriter.start();

            //初始化 数据传输备份提供者
            DataTransferBackupProvider dataTransferBackupProvider = SwordDataTransferBackupProvider.SwordDataTransferBackupProviderBuilder.create()
                    .bindingDataSource(rightRandomQueue, leftOrderlyQueue)
                    .bind(backupPort)
                    .build();
            dataTransferBackupProvider.start();
        }else {
            NodeNamingInfo masterNodeNamingInfo = masterSlaveServiceCoordinator.getMasterNodeNamingInfo((DataEvent<NodeNamingInfo> dataEvent)->{
                NodeNamingInfo changeNodeNamingInfo = dataEvent.getData();
                switch (dataEvent.getType()){
                    case NODE_MASTER_DATA_CHANGE:
                        break;
                    case NODE_MASTER_DATA_DELETE:

                        break;
                }
            });
            String masterHost = masterNodeNamingInfo.getHost();
            int masterBackupPort = masterNodeNamingInfo.getPort();
            //初始化 数据传输备份收集器
            DataTransferBackupGather dataTransferBackupGather = SwordDataTransferBackupGather.SwordDataTransferBackupGatherBuilder.create()
                    .bindingTargetDataSource(leftOrderlyQueue, rightRandomQueue)
                    .connect(masterHost, masterBackupPort)
                    .build();
            dataTransferBackupGather.start();
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
        nodeId.setDcName(getParam("node.id.dc.name"));
        nodeId.setUnitCategoryName(getParam("node.id.unit.category.name"));
        nodeId.setUnitName(getParam("node.id.unit.name"));
        nodeId.setServerName(getParam("node.id.server.name"));
        return nodeId;
    }

    private String getParam(String key){
        return environment.getProperty(key);
    }
    private <T> T getParam(String key, Class<T> targetClass){
        return environment.getProperty(key, targetClass);
    }

}
