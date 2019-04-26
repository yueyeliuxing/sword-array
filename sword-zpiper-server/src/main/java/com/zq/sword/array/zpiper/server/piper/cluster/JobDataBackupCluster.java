package com.zq.sword.array.zpiper.server.piper.cluster;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.InterPiperProtocol;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.job.dto.*;
import com.zq.sword.array.zpiper.server.piper.job.processor.BackupDataRespProcessor;
import com.zq.sword.array.zpiper.server.piper.job.processor.ReplicateTaskBackupProcessor;
import com.zq.sword.array.zpiper.server.piper.job.processor.WriteTaskBackupProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 任务数据集群备份器
 * @author: zhouqi1
 * @create: 2019-04-26 19:48
 **/
public class JobDataBackupCluster {

    public static final Map<String, JobDataBackupCluster> JOB_DATA_BACKUP_CLUSTERS = new ConcurrentHashMap<>();

    private String jobName;

    /**
     * Job环境集群处理
     */
    private PiperNameProtocol piperNameProtocol;

    private Map<String, ReplicateDataBackuper> replicateDataBackupers;

    private ReplicateTaskBackupProcessor replicateTaskBackupProcessor;

    private WriteTaskBackupProcessor writeTaskBackupProcessor;

    private TaskExecutor taskExecutor;

    public synchronized static JobDataBackupCluster get(String jobName){
        return JOB_DATA_BACKUP_CLUSTERS.get(jobName);
    }

    public JobDataBackupCluster(String jobName, List<String> backupPipers, PiperNameProtocol piperNameProtocol) {
        this.jobName = jobName;
        this.piperNameProtocol = piperNameProtocol;
        this.piperNameProtocol.addJobCommandListener(new JobBackupCommandEventListener());

        this.replicateDataBackupers = new ConcurrentHashMap<>();
        assignReplicateDataBackupers(backupPipers);

        this.taskExecutor = new SingleTaskExecutor();

        JOB_DATA_BACKUP_CLUSTERS.put(jobName, this);
    }

    /**
     * 为需要复制的piper赋值
     * @param backupPiperLocations
     */
    private void assignReplicateDataBackupers(List<String> backupPiperLocations) {
        if(backupPiperLocations != null && !backupPiperLocations.isEmpty()){
            for (String backupPiperLocation : backupPiperLocations){
                ReplicateDataBackuper backuper = new ReplicateDataBackuper(backupPiperLocation);
                replicateDataBackupers.put(backupPiperLocation, backuper);
            }
        }
    }

    /**
     * 设置replicateTask 备份处理器
     * @param replicateTaskBackupProcessor
     */
    public void setReplicateTaskBackupProcessor(ReplicateTaskBackupProcessor replicateTaskBackupProcessor) {
        this.replicateTaskBackupProcessor = replicateTaskBackupProcessor;
    }

    /**
     * 设置writeTask 备份处理器
     * @param writeTaskBackupProcessor
     */
    public void setWriteTaskBackupProcessor(WriteTaskBackupProcessor writeTaskBackupProcessor) {
        this.writeTaskBackupProcessor = writeTaskBackupProcessor;
    }

    /**
     * 备份获取的复制数据
     * @param replicateData
     */
    public void backupReplicateData(ReplicateData replicateData){
        //异步发送数据到备份机器上
        if(replicateDataBackupers != null && !replicateDataBackupers.isEmpty()){
            for (ReplicateDataBackuper backuper : replicateDataBackupers.values()){
                taskExecutor.execute(()->{
                    backuper.backupReplicateData(replicateData);
                });
            }
        }
    }


    /**
     * 备份消费的offset
     * @param consumeNextOffset
     */
    public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset){
        //异步发送数据到备份机器上
        if(replicateDataBackupers != null && !replicateDataBackupers.isEmpty()){
            for (ReplicateDataBackuper backuper : replicateDataBackupers.values()){
                taskExecutor.execute(()->{
                    backuper.backupConsumeNextOffset(consumeNextOffset);
                });
            }
        }
    }

    /**
     * 任务命令监听器
     */
    private class JobBackupCommandEventListener implements HotspotEventListener<JobCommand> {

        private Logger logger = LoggerFactory.getLogger(JobBackupCommandEventListener.class);

        @Override
        public void listen(HotspotEvent<JobCommand> dataEvent) {
            JobCommand jobCommand = dataEvent.getData();
            JobType jobType = JobType.toType(jobCommand.getType());
            if(jobType == null){
                return;
            }
            switch (jobType){
                case BACKUP_PIPERS_CHANGE:
                    List<String> incrementBackupPipers = jobCommand.getIncrementBackupPipers();
                    if(incrementBackupPipers != null && !incrementBackupPipers.isEmpty()){
                        for (String backupPiperLocation : incrementBackupPipers){
                            ReplicateDataBackuper backuper = new ReplicateDataBackuper(backupPiperLocation);
                            replicateDataBackupers.put(backupPiperLocation, backuper);
                        }
                    }

                    List<String> decreaseBackupPipers = jobCommand.getDecreaseBackupPipers();
                    if(decreaseBackupPipers != null && !decreaseBackupPipers.isEmpty()){
                        for (String backupPiperLocation : decreaseBackupPipers){
                            ReplicateDataBackuper backuper = replicateDataBackupers.get(backupPiperLocation);
                            backuper.close();
                            replicateDataBackupers.remove(backupPiperLocation);
                        }
                    }
                    break;
                default:
                    break;
            }
            logger.info("获取PiperNamer命令:{}", jobCommand);
        }
    }

    /**
     * 创建InterPiperClient
     * @param piperLocation
     * @return
     */
    private InterPiperProtocol.InterPiperClient createBackupInterPiperClient(String piperLocation) {
        InterPiperProtocol.InterPiperClient interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(InterPiperProtocol.InterPiperClient.BACKUP_TYPE,
                jobName, piperLocation);
        interPiperClient.setBackupDataRespProcessor(new DefaultBackupDataRespProcessor());
        return interPiperClient;
    }

    /**
     * 默认的备份处理器
     */
    public class DefaultBackupDataRespProcessor implements BackupDataRespProcessor {

        @Override
        public void backupReplicateData(ReplicateDataId replicateDataId) {
            replicateTaskBackupProcessor.backupReplicateData(replicateDataId);
        }

        @Override
        public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
            writeTaskBackupProcessor.backupConsumeNextOffset(consumeNextOffset);
        }
    }

    /**
     * 分片数据复制器
     */
    private class ReplicateDataBackuper {

        private Logger logger = LoggerFactory.getLogger(ReplicateDataBackuper.class);

        private InterPiperProtocol.InterPiperClient interPiperClient;

        public ReplicateDataBackuper(String replicatePiperLocation) {
            String[] groupLocations = replicatePiperLocation.split("\\|");
            this.interPiperClient = createBackupInterPiperClient(groupLocations[1]);
        }

        /**
         * 备份获取的复制数据
         * @param replicateData
         */
        public void backupReplicateData(ReplicateData replicateData){
            interPiperClient.sendReplicateData(replicateData);
        }

        /**
         * 备份消费的offset
         * @param consumeNextOffset
         */
        public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset){
            interPiperClient.sendConsumeNextOffset(consumeNextOffset);
        }


        public void close() {
            interPiperClient.disconnect();
        }
    }

    /**
     * 关闭
     */
    public void close(){
        //异步发送数据到备份机器上
        if(replicateDataBackupers != null && !replicateDataBackupers.isEmpty()){
            for (ReplicateDataBackuper backuper : replicateDataBackupers.values()){
                backuper.close();
            }
        }
    }

}
