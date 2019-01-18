package com.zq.sword.array.zpiper.server;

import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;
import com.zq.sword.array.data.bridge.SwordCommandCycleDisposeBridge;
import com.zq.sword.array.data.structure.queue.ResourceQueue;
import com.zq.sword.array.mq.jade.bitcask.BitcaskRightRandomQueue;
import com.zq.sword.array.metadata.ConfigManager;
import com.zq.sword.array.metadata.DataConsumerServiceCoordinator;
import com.zq.sword.array.metadata.MasterSlaveServiceCoordinator;
import com.zq.sword.array.metadata.MetadataCenter;
import com.zq.sword.array.metadata.data.*;
import com.zq.sword.array.metadata.impl.ZkMatedataCenter;
import com.zq.sword.array.redis.replicator.EmbeddedSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.writer.RedisCommandWriter;
import com.zq.sword.array.redis.writer.RedisConfig;
import com.zq.sword.array.redis.writer.SwordRedisCommandWriter;
import com.zq.sword.array.transfer.gather.DataTransferGather;
import com.zq.sword.array.transfer.gather.SwordDataTransferGather;
import com.zq.sword.array.transfer.provider.DataTransferProvider;
import com.zq.sword.array.transfer.provider.SwordDataTransferProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(SwordServerStarter.class);

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

        logger.info("metadata 模块开始启动");
        //1.zk获取参数
        String connectAddr = getParam("zk.connect.address");
        int timeout = getParam("zk.connect.timeout", Integer.class);
        MetadataCenter metadataCenter = new ZkMatedataCenter(connectAddr, timeout);
        MasterSlaveServiceCoordinator masterSlaveServiceCoordinator = metadataCenter.getMasterSlaveServiceCoordinator(nodeId);
        ConfigManager configManager = metadataCenter.getConfigManager(nodeId);
        DataConsumerServiceCoordinator dataConsumerServiceCoordinator = metadataCenter.getDataConsumerServiceCoordinator(nodeId);
        logger.info("metadata 模块启动成功");

        masterSlaveServiceCoordinator.setMasterStaterState(MasterStaterState.STARTING);
        logger.info("metadata 设置服务为启动中");

        //初始化data-birdge
        DataCycleDisposeBridge<RedisCommand> dataCycleDisposeBridge = new SwordCommandCycleDisposeBridge();

        //初始化R-queue
        String rightDataFilePath = getParam("data.right.queue.file.path");
        String rightIndexFilePath = getParam("data.right.queue.index.file.path");
        RightRandomQueue rightRandomQueue = new BitcaskRightRandomQueue(rightDataFilePath, rightIndexFilePath);
        rightRandomQueue.bindingDataCycleDisposeBridge(dataCycleDisposeBridge);

        //初始化L-Queue
        String leftDataFilePath = getParam("data.left.queue.file.path");
        ResourceQueue<SwordData> leftOrderlyQueue = new StoredWrapDataQueue(leftDataFilePath);
        leftOrderlyQueue.bindingDataCycleDisposeBridge(dataCycleDisposeBridge);

        //判断node 身份是 master 还是slave
        NodeNamingInfo nodeNamingInfo = new NodeNamingInfo(host, port, backupPort);
        NodeInfo nodeInfo = masterSlaveServiceCoordinator.register(nodeNamingInfo);
        NodeRole nodeRole = nodeInfo.getRole();
        if(nodeRole.equals(NodeRole.PIPER_MASTER)){

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
          /*  DataTransferBackupProvider dataTransferBackupProvider = SwordDataTransferBackupProvider.SwordDataTransferBackupProviderBuilder.create()
                    .bindingDataSource(rightRandomQueue, leftOrderlyQueue)
                    .bind(backupPort)
                    .build();
            dataTransferBackupProvider.start();*/

            //初始化redis slave replicator
            long workId = getParam("redis.replicator.work.id", Long.class);
            long datacenterId = getParam("redis.replicator.datacenter.id", Long.class);
            String redisUri = getParam("redis.replicator.redis.uri");
            SlaveRedisReplicator slaveRedisReplicator = EmbeddedSlaveRedisReplicator.SwordSlaveRedisReplicatorBuilder.create()
                    .idGenerat(workId, datacenterId)
                    .bindingDataSource(rightRandomQueue)
                    .listen(redisUri)
                    .build();
            slaveRedisReplicator.start();

            boolean switchTag = true;
            while (switchTag){
                while (dataTransferProvider.started()){
                    masterSlaveServiceCoordinator.setMasterStaterState(MasterStaterState.STARTED);
                    logger.info("metadata 设置服务为启动成功");
                    switchTag = false;
                    break;
                }
            }

        }/*else {
            NodeNamingInfo masterNodeNamingInfo = masterSlaveServiceCoordinator.getMasterNodeNamingInfo((DataEvent<NodeNamingInfo> dataEvent)->{
                NodeNamingInfo changeNodeNamingInfo = dataEvent.getData();
                switch (dataEvent.getType()){
                    case NODE_MASTER_DATA_CHANGE:
                        break;
                    case NODE_MASTER_DATA_DELETE:

                        break;
                    default:
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
        }*/


    }

    private RedisConfig getRedisConfig() {
       /* String host = getParam("redis.host");
        String port = getParam("redis.port");
        String pass = getParam("redis.pass");
        String timeout = getParam("redis.timeout");
        String maxIdle = getParam("redis.maxIdle");
        String maxTotal = getParam("redis.maxTotal");
        String maxWaitMillis = getParam("redis.maxWaitMillis");
        String testOnBorrow = getParam("redis.testOnBorrow");*/
        String host = "127.0.0.1";
        String port = "6379";
        String pass = null;
        String timeout = "100000";
        String maxIdle = "10";
        String maxTotal = "100";
        String maxWaitMillis = "30000";
        String testOnBorrow =  null;
        return new RedisConfig(host, port, pass, timeout, maxIdle, maxTotal, maxWaitMillis, testOnBorrow);
    }

    private NodeId buildNodeId() {
        NodeId nodeId = new NodeId();
        nodeId.setType(NodeType.valueOf(getParam("node.id.type")));
        nodeId.setDc(getParam("node.id.dc.name"));
        nodeId.setUnitCategory(getParam("node.id.unit.category.name"));
        nodeId.setUnit(getParam("node.id.unit.name"));
        nodeId.setGroup(getParam("node.id.group.name"));
        return nodeId;
    }

    private String getParam(String key){
        return environment.getProperty(key);
    }
    private <T> T getParam(String key, Class<T> targetClass){
        return environment.getProperty(key, targetClass);
    }

}
