package com.zq.sword.array.zpiper.transfer;

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

public class TransferProviderTest {


    public void testTransferProvider(){
    }

}
