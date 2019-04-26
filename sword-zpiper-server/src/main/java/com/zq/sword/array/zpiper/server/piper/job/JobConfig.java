package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.zpiper.server.piper.NamePiper;
import com.zq.sword.array.zpiper.server.piper.job.command.JobCommand;
import lombok.Getter;

/**
 * @program: sword-array
 * @description: Job配置
 * @author: zhouqi1
 * @create: 2019-04-25 16:49
 **/
@Getter
public class JobConfig {
    private JobCommand jobCommand;
    private NamePiper namePiper;
    private PartitionSystem broker;

    public JobConfig(JobCommand jobCommand, NamePiper namePiper, PartitionSystem broker) {
        this.jobCommand = jobCommand;
        this.namePiper = namePiper;
        this.broker = broker;
    }
}
