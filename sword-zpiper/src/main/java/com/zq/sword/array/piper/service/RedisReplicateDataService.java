package com.zq.sword.array.piper.service;

import com.zq.sword.array.piper.storage.RedisDataStorage;
import com.zq.sword.array.rpc.api.piper.ReplicateDataService;
import com.zq.sword.array.rpc.api.piper.dto.ConsumeNextOffset;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateData;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateDataId;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateDataQuery;

import java.util.List;

/**
 * @program: sword-array
 * @description: redis数据服务
 * @author: zhouqi1
 * @create: 2019-06-14 14:19
 **/
public class RedisReplicateDataService implements ReplicateDataService {

    /**
     * 任务数据存储
     */
    private RedisDataStorage redisDataStorage;

    public RedisReplicateDataService(RedisDataStorage redisDataStorage) {
        this.redisDataStorage = redisDataStorage;
    }

    @Override
    public List<ReplicateData> listReplicateData(ReplicateDataQuery replicateDataQuery) {
        return redisDataStorage.readReplicateData(replicateDataQuery);
    }

    @Override
    public ReplicateDataId addReplicateData(ReplicateData replicateData) {
        long offset = redisDataStorage.writeReplicateData(replicateData);
        return new ReplicateDataId(replicateData.getPiperGroup(), replicateData.getJobName(), offset);
    }

    @Override
    public long addConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
        redisDataStorage.writeConsumedNextOffset(consumeNextOffset);
        return consumeNextOffset.getNextOffset();
    }
}
