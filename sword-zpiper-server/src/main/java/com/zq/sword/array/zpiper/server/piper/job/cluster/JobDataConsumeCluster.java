package com.zq.sword.array.zpiper.server.piper.job.cluster;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.zpiper.server.piper.protocol.InterPiperProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.PiperNameProtocol;
import com.zq.sword.array.zpiper.server.piper.job.dto.JobCommand;
import com.zq.sword.array.zpiper.server.piper.job.dto.JobType;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;
import com.zq.sword.array.zpiper.server.piper.job.processor.ConsumeDataRespProcessor;
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
public class JobDataConsumeCluster {

    public static final Map<String, JobDataConsumeCluster> JOB_DATA_CONSUME_CLUSTERS = new ConcurrentHashMap<>();

    private String jobName;

    /**
     * Job环境集群处理
     */
    private PiperNameProtocol piperNameProtocol;

    private Map<String, DataConsumer> partitionConsumers;

    private PartitionConsumerBuilder partitionConsumerBuilder;

    public synchronized static JobDataConsumeCluster get(String jobName){
        return JOB_DATA_CONSUME_CLUSTERS.get(jobName);
    }

    public JobDataConsumeCluster(String jobName, List<String> consumePipers, PiperNameProtocol piperNameProtocol) {
        this.jobName = jobName;
        this.piperNameProtocol = piperNameProtocol;
        this.piperNameProtocol.addJobCommandListener(new JobConsumeCommandEventListener());

        this.partitionConsumers = new ConcurrentHashMap<>();
        assignReplicateDataConsumers(consumePipers);

        JOB_DATA_CONSUME_CLUSTERS.put(jobName, this);
    }

    /**
     * 为需要复制的piper赋值
     * @param consumePiperLocations
     */
    private void assignReplicateDataConsumers(List<String> consumePiperLocations) {
        if(consumePiperLocations != null && !consumePiperLocations.isEmpty()){
            for (String consumePiperLocation : consumePiperLocations){
                DataConsumer consumer = newPartitionConsumer(consumePiperLocation);
                partitionConsumers.put(consumePiperLocation, consumer);
            }
        }
    }

    public void setPartitionConsumerBuilder(PartitionConsumerBuilder partitionConsumerBuilder) {
        this.partitionConsumerBuilder = partitionConsumerBuilder;
    }

    /**
     * 任务命令监听器
     */
    private class JobConsumeCommandEventListener implements HotspotEventListener<JobCommand> {

        private Logger logger = LoggerFactory.getLogger(JobConsumeCommandEventListener.class);

        @Override
        public void listen(HotspotEvent<JobCommand> dataEvent) {
            JobCommand jobCommand = dataEvent.getData();
            JobType jobType = JobType.toType(jobCommand.getType());
            if(jobType == null){
                return;
            }
            switch (jobType){
                case CONSUME_PIPERS_CHANGE:
                    List<String> incrementConsumePipers = jobCommand.getIncrementConsumePipers();
                    if(incrementConsumePipers != null && !incrementConsumePipers.isEmpty()){
                        for (String consumePiperLocation : incrementConsumePipers){
                            DataConsumer consumer = newPartitionConsumer(consumePiperLocation);
                            partitionConsumers.put(consumePiperLocation, consumer);
                        }
                    }

                    List<String> decreaseConsumePipers = jobCommand.getDecreaseConsumePipers();
                    if(decreaseConsumePipers != null && !decreaseConsumePipers.isEmpty()){
                        for (String consumePiperLocation : decreaseConsumePipers){
                            DataConsumer consumer = partitionConsumers.get(consumePiperLocation);
                            consumer.stop();
                            partitionConsumers.remove(consumePiperLocation);
                        }
                    }
                    break;
                default:
                    break;
            }
            logger.info("获取PiperNamer命令:{}", jobCommand);
        }
    }

    public DataConsumer newPartitionConsumer(String consumePiperLocation){
        return partitionConsumerBuilder.build(consumePiperLocation);
    }

    /**
     * 构建器
     */
    public interface PartitionConsumerBuilder {
        DataConsumer build(String consumePiperLocation);
    }

    /**
     * 消息消费者
     */
    public static abstract class DataConsumer extends AbstractThreadActuator implements Actuator {

        private Logger logger = LoggerFactory.getLogger(DataConsumer.class);

        private InterPiperProtocol.InterPiperClient interPiperClient;

        public DataConsumer(String jobName, String targetPiperLocation) {
            String[] groupLocations = targetPiperLocation.split("\\|");
            this.interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(InterPiperProtocol.InterPiperClient.CONSUME_TYPE, jobName, groupLocations[1]);
            this.interPiperClient.setConsumeDataRespProcessor(new ConsumeDataRespProcessor() {
                @Override
                public void consumeReplicateData(List<ReplicateData> replicateData) {
                    DataConsumer.this.consumeReplicateData(replicateData);
                }
            });
        }

        /**
         * 消费数据
         * @param replicateDatas
         */
        protected abstract void consumeReplicateData(List<ReplicateData> replicateDatas);

        /**
         * 消费数据
         * @param replicateDataReq
         */
        public void consumeReplicateDataReq(ReplicateDataReq replicateDataReq){
            interPiperClient.sendReplicateDataReq(replicateDataReq);
        }

        @Override
        public abstract void run();

        @Override
        public void stop() {
            super.stop();
            interPiperClient.disconnect();
        }
    }

    /**
     * 关闭
     */
    public void close(){
        for(DataConsumer partitionConsumer : partitionConsumers.values()){
            partitionConsumer.stop();
        }
    }

}
