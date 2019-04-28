package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.command.JobCommand;
import com.zq.sword.array.network.rpc.protocol.dto.command.JobType;
import com.zq.sword.array.network.rpc.protocol.dto.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.data.ReplicateDataReq;
import com.zq.sword.array.network.rpc.protocol.processor.JobCommandProcessor;
import com.zq.sword.array.network.rpc.protocol.processor.ReplicateDataReqProcessor;
import com.zq.sword.array.network.rpc.protocol.dto.monitor.TaskMonitor;
import com.zq.sword.array.piper.job.storage.JobRuntimeStorage;
import com.zq.sword.array.piper.job.storage.LocalJobRuntimeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: Job控制器
 * @author: zhouqi1
 * @create: 2019-04-26 14:12
 **/
public class JobController implements JobCommandProcessor, ReplicateDataReqProcessor {

    private Logger logger = LoggerFactory.getLogger(JobController.class);

    /**
     * 数据分片存储系统
     */
    private JobRuntimeStorage jobRuntimeStorage;

    private JobSystem jobSystem;

    public JobController(String jobRuntimeStoragePath, TaskMonitor taskMonitor) {
        this.jobRuntimeStorage = new LocalJobRuntimeStorage(jobRuntimeStoragePath);
        this.jobSystem = new JobSystem(jobRuntimeStorage, taskMonitor);
    }

    /**
     * 处理Job控制相关的命令
     *
     * @param jobCommand
     */
    @Override
    public void accept(JobCommand jobCommand) {
        JobType jobType = JobType.toType(jobCommand.getType());
        if (jobType == null) {
            return;
        }
        Job job = null;
        switch (jobType) {
            case JOB_NEW:
                //创建Job
                jobSystem.createJob(new JobEnv(jobCommand.getName(), jobCommand.getPiperGroup(),
                        jobCommand.getSourceRedis(), jobCommand.getBackupPipers(), jobCommand.getConsumePipers()));
                break;
            case JOB_START:
                //开启
                jobSystem.startJob(jobCommand.getName());
                break;
            case JOB_DESTROY:
                //销毁job
                jobSystem.destroyJob(jobCommand.getName());
                break;
            case REPLICATE_TASK_RESTART:
                //重启replicate-task
                job = jobSystem.getJob(jobCommand.getName());
                job.restartReplicateTask();
                break;
            case WRITE_TASK_RESTART:
                //重启write-task
                job = jobSystem.getJob(jobCommand.getName());
                job.restartWriteTask();
                break;
            case BACKUP_PIPERS_CHANGE:
                //备份piper 改变
                job = jobSystem.getJob(jobCommand.getName());
                job.flushJobBackupPipers(jobCommand.getIncrementBackupPipers(), jobCommand.getDecreaseBackupPipers());
                break;
            case CONSUME_PIPERS_CHANGE:
                //消费piper 改变
                job = jobSystem.getJob(jobCommand.getName());
                job.flushJobConsumePipers(jobCommand.getIncrementConsumePipers(), jobCommand.getDecreaseConsumePipers());
                break;
            default:
                break;
        }
        logger.info("获取PiperNamer命令:{}", jobCommand);
    }

    @Override
    public List<ReplicateData> handleReplicateDataReq(ReplicateDataReq req) {
        return jobRuntimeStorage.readReplicateData(req);
    }

    @Override
    public void handleReplicateData(ReplicateData replicateData) {
        jobRuntimeStorage.writeReplicateData(replicateData);
    }

    @Override
    public void handleConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
        jobRuntimeStorage.writeConsumedNextOffset(consumeNextOffset);
    }

}


