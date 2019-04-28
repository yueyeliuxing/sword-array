package com.zq.sword.array.zpiper.server.piper.job.cluster;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskHealth;
import com.zq.sword.array.zpiper.server.piper.job.monitor.TaskMonitor;
import com.zq.sword.array.zpiper.server.piper.job.processor.ReplicateDataReqProcessor;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperServiceProtocol;
import com.zq.sword.array.zpiper.server.piper.job.dto.*;
import com.zq.sword.array.zpiper.server.piper.job.*;
import com.zq.sword.array.zpiper.server.piper.job.storage.JobRuntimeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: Job相关资源环境集群处理
 * @author: zhouqi1
 * @create: 2019-04-26 14:12
 **/
public class JobControlCluster {

    private Logger logger = LoggerFactory.getLogger(JobControlCluster.class);

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

    private Map<String, JobDataBackupCluster> jobDataBackupClusters;

    public JobControlCluster(PiperNameProtocol piperNameProtocol, PiperServiceProtocol piperServiceProtocol, String jobRuntimeStoragePath) {
        this.piperNameProtocol = piperNameProtocol;
        this.piperServiceProtocol = piperServiceProtocol;
        this.jobRuntimeStorage = new JobRuntimeStorage(jobRuntimeStoragePath);
        this.jobSystem = JobSystem.getInstance();
        this.jobDataBackupClusters = new ConcurrentHashMap<>();

        this.piperNameProtocol.addJobCommandListener(new JobCommandEventListener());
        this.piperServiceProtocol.setJobRuntimeStorageProcessor(new DefaultReplicateDataReqProcessor());
    }

    /**
     * 任务命令监听器
     */
    private class JobCommandEventListener implements HotspotEventListener<JobCommand> {

        private Logger logger = LoggerFactory.getLogger(JobCommandEventListener.class);

        @Override
        public void listen(HotspotEvent<JobCommand> dataEvent) {
            JobCommand jobCommand = dataEvent.getData();
            JobType jobType = JobType.toType(jobCommand.getType());
            if(jobType == null){
                return;
            }
            Job job = null;
            JobDataBackupCluster jobDataBackupCluster = null;
            switch (jobType){
                case JOB_NEW:
                    //创建备份器
                    jobDataBackupCluster = new JobDataBackupCluster(jobCommand.getName(),
                            jobCommand.getBackupPipers(), piperNameProtocol);
                    jobDataBackupClusters.put(jobCommand.getName(), jobDataBackupCluster);

                    //创建Job
                    jobSystem.createJob(new JobEnv(jobCommand.getName(), jobCommand.getPiperGroup(),
                            jobCommand.getSourceRedis()), jobRuntimeStorage, new JobTaskMonitor());
                    break;
                case JOB_START:
                    jobSystem.startJob(jobCommand.getName());
                    break;
                case JOB_DESTROY:
                    //销毁job
                    jobSystem.destroyJob(jobCommand.getName());

                    //关闭备份器
                    jobDataBackupCluster = jobDataBackupClusters.get(jobCommand.getName());
                    if(jobDataBackupCluster != null){
                        jobDataBackupCluster.close();
                        jobDataBackupClusters.remove(jobCommand.getName());
                    }
                    break;
                case REPLICATE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartReplicateTask();
                    break;
                case WRITE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartWriteTask();
                    break;
                default:
                    break;
            }
            logger.info("获取PiperNamer命令:{}", jobCommand);
        }
    }

    /**
     * Job健康监控器
     */
    private class JobTaskMonitor implements TaskMonitor {

        @Override
        public void monitor(TaskHealth health) {
            piperNameProtocol.reportJobHealth(health);
        }
    }

    /**
     * 默认的Job运行时存储处理器
     */
    private class DefaultReplicateDataReqProcessor implements ReplicateDataReqProcessor {
        @Override
        public List<ReplicateData> obtainReplicateData(ReplicateDataReq req) {
            return jobRuntimeStorage.readReplicateData(req);
        }

        @Override
        public void writeReplicateData(ReplicateData replicateData) {
            jobRuntimeStorage.writeReplicateData(replicateData);
        }

        @Override
        public void writeConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
            jobRuntimeStorage.writeConsumeNextOffset(consumeNextOffset);
        }
    }
}
