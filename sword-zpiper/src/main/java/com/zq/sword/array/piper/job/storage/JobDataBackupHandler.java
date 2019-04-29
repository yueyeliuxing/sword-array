package com.zq.sword.array.piper.job.storage;

import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.processor.BackupDataRespProcessor;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.InterPiperProtocol;
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
public class JobDataBackupHandler {

    private String jobName;

    private Map<String, ReplicateDataBackuper> replicateDataBackupers;

    private BackupDataRespProcessor backupDataRespProcessor;

    private TaskExecutor taskExecutor;

    public JobDataBackupHandler(String jobName, List<String> backupPipers) {
        this.jobName = jobName;
        this.replicateDataBackupers = new ConcurrentHashMap<>();
        assignReplicateDataBackupers(backupPipers);

        this.taskExecutor = new SingleTaskExecutor();
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
     * 设置数据备份成功处理器
     * @param backupDataRespProcessor
     */
    public void setBackupDataRespProcessor(BackupDataRespProcessor backupDataRespProcessor) {
        this.backupDataRespProcessor = backupDataRespProcessor;
    }

    /**
     * 处理备份piper改变
     * @param incrementBackupPipers
     * @param decreaseBackupPipers
     */
    public void handleBackupPiperChange(List<String> incrementBackupPipers,  List<String> decreaseBackupPipers){
        if(incrementBackupPipers != null && !incrementBackupPipers.isEmpty()){
            for (String backupPiperLocation : incrementBackupPipers){
                ReplicateDataBackuper backuper = new ReplicateDataBackuper(backupPiperLocation);
                replicateDataBackupers.put(backupPiperLocation, backuper);
            }
        }

        if(decreaseBackupPipers != null && !decreaseBackupPipers.isEmpty()){
            for (String backupPiperLocation : decreaseBackupPipers){
                ReplicateDataBackuper backuper = replicateDataBackupers.get(backupPiperLocation);
                backuper.close();
                replicateDataBackupers.remove(backupPiperLocation);
            }
        }
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
     * 创建InterPiperClient
     * @param piperLocation
     * @return
     */
    private InterPiperProtocol.InterPiperClient createBackupInterPiperClient(String piperLocation) {
        InterPiperProtocol.InterPiperClient interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(InterPiperProtocol.InterPiperClient.BACKUP_TYPE,
                jobName, piperLocation);
        interPiperClient.setBackupDataRespProcessor(backupDataRespProcessor);
        return interPiperClient;
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
    public void destroy(){
        //异步发送数据到备份机器上
        if(replicateDataBackupers != null && !replicateDataBackupers.isEmpty()){
            for (ReplicateDataBackuper backuper : replicateDataBackupers.values()){
                backuper.close();
            }
        }
    }

}
