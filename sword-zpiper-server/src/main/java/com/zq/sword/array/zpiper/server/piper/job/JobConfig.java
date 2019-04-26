package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.data.storage.PartitionSystem;
import lombok.Getter;

/**
 * @program: sword-array
 * @description: Job配置
 * @author: zhouqi1
 * @create: 2019-04-25 16:49
 **/
@Getter
public class JobConfig {
    private JobEnv jobEnv;
    private PartitionSystem partitionSystem;

    public JobConfig(JobEnv jobEnv, PartitionSystem partitionSystem) {
        this.jobEnv = jobEnv;
        this.partitionSystem = partitionSystem;
    }
}
