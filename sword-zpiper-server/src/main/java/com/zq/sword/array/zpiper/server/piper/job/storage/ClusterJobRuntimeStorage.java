package com.zq.sword.array.zpiper.server.piper.job.storage;

import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataId;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;
import com.zq.sword.array.zpiper.server.piper.protocol.processor.BackupDataRespProcessor;

import java.util.List;

/**
 * @program: sword-array
 * @description: Job数据同步集群数据存储
 * @author: zhouqi1
 * @create: 2019-04-28 13:49
 **/
public class ClusterJobRuntimeStorage implements JobRuntimeStorage {

    private JobDataBackupHandler jobDataBackupHandler;

    private JobRuntimeStorage jobRuntimeStorage;

    public ClusterJobRuntimeStorage(String jobName, List<String> backupPipers, JobRuntimeStorage jobRuntimeStorage) {
        this.jobDataBackupHandler = new JobDataBackupHandler(jobName, backupPipers);
        this.jobDataBackupHandler.setBackupDataRespProcessor(new BackupDataRespProcessor() {
            @Override
            public void backupReplicateData(ReplicateDataId replicateDataId) {

            }

            @Override
            public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {

            }
        });
        this.jobRuntimeStorage = jobRuntimeStorage;
    }

    @Override
    public long writeReplicateData(ReplicateData replicateData) {
        long offset = jobRuntimeStorage.writeReplicateData(replicateData);
        jobDataBackupHandler.backupReplicateData(new ReplicateData(replicateData.getPiperGroup(), replicateData.getJobName(), offset, replicateData.getData()));
        return offset;
    }

    @Override
    public List<ReplicateData> readReplicateData(ReplicateDataReq req) {
        return jobRuntimeStorage.readReplicateData(req);
    }

    @Override
    public long getConsumeNextOffset(String piperGroup, String jobName) {
        return jobRuntimeStorage.getConsumeNextOffset(piperGroup, jobName);
    }

    @Override
    public void writeConsumedNextOffset(ConsumeNextOffset consumeNextOffset) {
        jobRuntimeStorage.writeConsumedNextOffset(consumeNextOffset);

        //备份到其他piper上
        jobDataBackupHandler.backupConsumeNextOffset(consumeNextOffset);
    }

    /**
     * 刷新任务备份piper
     * @param incrementBackupPipers
     * @param decreaseBackupPipers
     */
    public void flushJobBackupPipers(List<String> incrementBackupPipers, List<String> decreaseBackupPipers){
        jobDataBackupHandler.handleBackupPiperChange(incrementBackupPipers, decreaseBackupPipers);
    }


    /**
     * 销毁
     */
    public void destroy(){
        jobDataBackupHandler.destroy();
    }
}
