package com.zq.sword.array.rpc.api.piper;

import com.zq.sword.array.rpc.api.piper.dto.ConsumeNextOffset;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateData;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateDataId;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateDataQuery;

import java.util.List;

/**
 * @program: sword-array
 * @description: Piper 任务数据服务
 * @author: zhouqi1
 * @create: 2019-06-14 11:57
 **/
public interface ReplicateDataService {

    /**
     * 获取指定消息
     * @param req
     * @return
     */
    List<ReplicateData> listReplicateData(ReplicateDataQuery req);

    /**
     * 处理指定消息
     * @param replicateData
     */
    ReplicateDataId addReplicateData(ReplicateData replicateData);

    /**
     * 写入要消费下一个的offset
     * @param consumeNextOffset
     */
    long addConsumeNextOffset(ConsumeNextOffset consumeNextOffset);
}
