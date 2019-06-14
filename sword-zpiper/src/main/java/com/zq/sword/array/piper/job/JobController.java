package com.zq.sword.array.piper.job;

import com.zq.sword.array.piper.storage.RedisDataStorage;
import com.zq.sword.array.rpc.api.namer.dto.JobCommand;
import com.zq.sword.array.rpc.api.namer.dto.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: Job控制器
 * @author: zhouqi1
 * @create: 2019-04-26 14:12
 **/
public class JobController {

    private Logger logger = LoggerFactory.getLogger(JobController.class);

    private JobSystem jobSystem;

    public JobController(RedisDataStorage redisDataStorage) {
        this.jobSystem = new JobSystem(redisDataStorage);
    }

    public void setJobMonitor(JobMonitor jobMonitor) {
        jobSystem.setJobMonitor(jobMonitor);
    }

    /**
     * 处理Job控制相关的命令
     *
     * @param jobCommand
     */
    public void handleJobCommand(JobCommand jobCommand) {
        JobType jobType = JobType.toType(jobCommand.getType());
        if (jobType == null) {
            return;
        }
        Job job = null;
        switch (jobType) {
            case JOB_NEW:
                //创建Job
                jobSystem.createJob(new JobContext(jobCommand.getName(), jobCommand.getPiperGroup(),
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
            case JOB_RESTART:
                //重启replicate-task
                job = jobSystem.getJob(jobCommand.getName());
                job.restart();
                break;
            case BACKUP_PIPERS_CHANGE:
                //备份piper 改变
                RedisJob redisJob = (RedisJob) jobSystem.getJob(jobCommand.getName());
                redisJob.flushJobBackupPipers(jobCommand.getIncrementBackupPipers(), jobCommand.getDecreaseBackupPipers());
                break;
            case CONSUME_PIPERS_CHANGE:
                //消费piper 改变
                redisJob = (RedisJob) jobSystem.getJob(jobCommand.getName());
                redisJob.flushJobConsumePipers(jobCommand.getIncrementConsumePipers(), jobCommand.getDecreaseConsumePipers());
                break;
            default:
                break;
        }
        logger.info("获取PiperNamer命令:{}", jobCommand);
    }

}


