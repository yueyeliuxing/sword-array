package com.zq.sword.array.piper.pipeline;

import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import com.zq.sword.array.network.rpc.protocol.processor.BackupDataResultProcessor;
import com.zq.sword.array.tasks.Actuator;
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
public class BackupPipeline implements RefreshPipeline<BackupData> {

    private String jobName;

    private Map<String, ReplicateDataBackuper> replicateDataBackupers;

    private volatile OutflowListener<BackupData> listener;

    public BackupPipeline(String jobName, List<String> backupPipers) {
        this.jobName = jobName;
        this.replicateDataBackupers = new ConcurrentHashMap<>();
        assignReplicateDataBackupers(backupPipers);
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
     * 创建InterPiperClient
     * @param piperLocation
     * @return
     */
    private InterPiperProtocol.InterPiperClient createBackupInterPiperClient(String piperLocation) {
        InterPiperProtocol.InterPiperClient interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(InterPiperProtocol.InterPiperClient.BACKUP_TYPE,
                jobName, piperLocation);
        interPiperClient.setBackupDataResultProcessor(new BackupDataResultProcessor(){

            @Override
            public void handleBackupReplicateDataResult(ReplicateDataId replicateDataId) {
                listener.outflow(new BackupData(BackupData.REPLICATE_DATA_RESP, replicateDataId));
            }

            @Override
            public void handleBackupConsumeNextOffsetResult(ConsumeNextOffset consumeNextOffset) {
                listener.outflow(new BackupData(BackupData.CONSUME_DATA_RESP, consumeNextOffset));
            }
        });
        return interPiperClient;
    }

    @Override
    public void refresh(PipeConfig config) {
        List<String> incrementBackupPipers = (List<String>)config.get("incrementBackupPipers");
        if(incrementBackupPipers != null && !incrementBackupPipers.isEmpty()){
            for (String backupPiperLocation : incrementBackupPipers){
                ReplicateDataBackuper backuper = new ReplicateDataBackuper(backupPiperLocation);
                replicateDataBackupers.put(backupPiperLocation, backuper);
            }
        }

        List<String> decreaseBackupPipers = (List<String>)config.get("decreaseBackupPipers");
        if(decreaseBackupPipers != null && !decreaseBackupPipers.isEmpty()){
            for (String backupPiperLocation : decreaseBackupPipers){
                ReplicateDataBackuper backuper = replicateDataBackupers.get(backupPiperLocation);
                backuper.stop();
                replicateDataBackupers.remove(backupPiperLocation);
            }
        }
    }

    @Override
    public void outflow(OutflowListener<BackupData> listener) {
        this.listener = listener;
    }

    @Override
    public void open() {

    }

    @Override
    public void inflow(BackupData data) {
        switch(data.getType()){
            case BackupData.REPLICATE_DATA :
                //异步发送数据到备份机器上
                callbackReplicateDataBackuper((backuper)->backuper.backupReplicateData((ReplicateData) data.getData()));
                break;
            case BackupData.CONSUME_DATA:
                //异步发送数据到备份机器上
                callbackReplicateDataBackuper((backuper)->backuper.backupConsumeNextOffset((ConsumeNextOffset) data.getData()));
                break;
            default:
                break;
        }

    }

    @Override
    public void close() {
        //关闭
        callbackReplicateDataBackuper((backuper)->backuper.stop());
    }

    /**
     * 回调处理
     * @param callback
     */
    private void callbackReplicateDataBackuper(Callback callback){
        //异步发送数据到备份机器上
        if(replicateDataBackupers != null && !replicateDataBackupers.isEmpty()){
            for (ReplicateDataBackuper backuper : replicateDataBackupers.values()){
                callback.call(backuper);
            }
        }
    }

    /**
     * 回调
     */
    private interface Callback {

        void call(ReplicateDataBackuper backuper);
    }

    /**
     * 分片数据复制器
     */
    private class ReplicateDataBackuper implements Actuator {

        private Logger logger = LoggerFactory.getLogger(ReplicateDataBackuper.class);

        private InterPiperProtocol.InterPiperClient interPiperClient;

        private TaskExecutor taskExecutor;

        public ReplicateDataBackuper(String replicatePiperLocation) {
            this.interPiperClient = createBackupInterPiperClient(replicatePiperLocation);
            this.taskExecutor = new SingleTaskExecutor();
        }

        /**
         * 备份获取的复制数据
         * @param replicateData
         */
        public void backupReplicateData(ReplicateData replicateData){
            taskExecutor.execute(()->interPiperClient.sendReplicateData(replicateData));
        }

        /**
         * 备份消费的offset
         * @param consumeNextOffset
         */
        public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset){
            taskExecutor.execute(()->interPiperClient.sendConsumeNextOffset(consumeNextOffset));
        }

        @Override
        public void start() {
            interPiperClient.connect();
        }

        @Override
        public void stop() {
            interPiperClient.disconnect();
        }
    }
}
