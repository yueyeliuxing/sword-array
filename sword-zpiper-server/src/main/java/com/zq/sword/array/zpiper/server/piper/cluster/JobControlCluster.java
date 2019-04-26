package com.zq.sword.array.zpiper.server.piper.cluster;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.zpiper.server.piper.job.*;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.JobCommand;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: Job相关资源环境集群处理
 * @author: zhouqi1
 * @create: 2019-04-26 14:12
 **/
public class JobControlCluster {

    /**
     * Job环境集群处理
     */
    private PiperNameProtocol piperNameProtocol;

    /**
     * 数据分片存储系统
     */
    private PartitionSystem partitionSystem;

    private JobSystem jobSystem;

    private Map<String, JobEnv> jobEnvs;

    public JobControlCluster(PiperNameProtocol piperNameProtocol, PartitionSystem partitionSystem) {
        this.piperNameProtocol = piperNameProtocol;
        this.piperNameProtocol.addJobCommandListener(new JobCommandEventListener());
        this.partitionSystem = partitionSystem;
        this.jobSystem = JobSystem.getInstance();
        this.jobEnvs = new ConcurrentHashMap<>();
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
            JobEnv jobEnv = null;
            switch (jobType){
                case JOB_NEW:
                    jobEnv = new JobEnv(jobCommand.getName(), jobCommand.getPiperGroup(),
                            jobCommand.getSourceRedis(), jobCommand.getReplicatePipers(), jobCommand.getTargetPipers());
                    jobSystem.createJob(new JobConfig(jobEnv, partitionSystem), new JobTaskMonitor());
                    jobEnvs.put(jobCommand.getName(), jobEnv);
                    break;
                case JOB_START:
                    jobSystem.startJob(jobCommand.getName());
                    break;
                case JOB_DESTROY:
                    jobSystem.destroyJob(jobCommand.getName());
                    break;
                case REPLICATE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartReplicateTask();
                    break;
                case WRITE_TASK_RESTART:
                    job = jobSystem.getJob(jobCommand.getName());
                    job.restartWriteTask();
                    break;
                case REPLICATE_PIPERS_CHANGE:
                    jobEnv = jobEnvs.get(jobCommand.getName());
                    jobEnv.emitterReplicatePiperChangeEvent(jobCommand.getIncrementReplicatePipers(), jobCommand.getIncrementTargetPipers());
                    break;
                case TARGET_PIPERS_CHANGE:
                    jobEnv = jobEnvs.get(jobCommand.getName());
                    jobEnv.emitterTargetPiperChangeEvent(jobCommand.getIncrementTargetPipers(), jobCommand.getDecreaseTargetPipers());
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
}
