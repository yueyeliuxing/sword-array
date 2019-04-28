package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.zpiper.server.piper.job.dto.*;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskHealth;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskMonitor;
import com.zq.sword.array.zpiper.server.piper.job.storage.JobRuntimeStorage;
import com.zq.sword.array.zpiper.server.piper.job.storage.LocalJobRuntimeStorage;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperServiceProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.processor.JobCommandProcessor;
import com.zq.sword.array.zpiper.server.piper.protocol.processor.ReplicateDataReqProcessor;
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
     * Job环境集群处理
     */
    private PiperNameProtocol piperNameProtocol;

    /**
     * 本Piper服务通信
     */
    private PiperServiceProtocol piperServiceProtocol;

    /**
     * 数据分片存储系统
     */
    private JobRuntimeStorage jobRuntimeStorage;

    private JobSystem jobSystem;

    private TaskMonitor taskMonitor;

    public JobController(String jobRuntimeStoragePath, TaskMonitor taskMonitor) {
        this.piperNameProtocol = piperNameProtocol;
        this.piperServiceProtocol = piperServiceProtocol;
        this.jobRuntimeStorage = new LocalJobRuntimeStorage(jobRuntimeStoragePath);

        this.jobSystem = JobSystem.getInstance();

        this.piperNameProtocol.setJobCommandProcessor(this);
        this.piperServiceProtocol.setJobRuntimeStorageProcessor((LocalJobRuntimeStorage)jobRuntimeStorage);
    }

    /**
     * 处理Job控制相关的命令
     * @param jobCommand
     */
    @Override
    public void accept(JobCommand jobCommand) {
        JobType jobType = JobType.toType(jobCommand.getType());
        if(jobType == null){
            return;
        }
        Job job = null;
        switch (jobType){
            case JOB_NEW:
                //创建Job
                jobSystem.createJob(new JobContext(jobCommand.getName(), jobCommand.getPiperGroup(),
                        jobCommand.getSourceRedis(), jobCommand.getBackupPipers(), jobCommand.getConsumePipers(),
                                jobRuntimeStorage),
                        new JobTaskMonitor());
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


