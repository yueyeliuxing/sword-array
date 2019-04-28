package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;
import com.zq.sword.array.zpiper.server.piper.protocol.processor.ConsumeDataRespProcessor;
import com.zq.sword.array.zpiper.server.piper.protocol.InterPiperProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 数据消费者池
 * @author: zhouqi1
 * @create: 2019-04-26 19:48
 **/
public class JobDataConsumerPool {

    private Map<String, DataConsumer> partitionConsumers;

    private PartitionConsumerBuilder partitionConsumerBuilder;

    public JobDataConsumerPool(List<String> consumePipers) {
        this.partitionConsumers = new ConcurrentHashMap<>();
        assignReplicateDataConsumers(consumePipers);
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
     * 处理消费的piper改变
     * @param incrementConsumePipers
     * @param decreaseConsumePipers
     */
    public void handleConsumePiperChange(List<String> incrementConsumePipers,  List<String> decreaseConsumePipers){
        if(incrementConsumePipers != null && !incrementConsumePipers.isEmpty()){
            for (String consumePiperLocation : incrementConsumePipers){
                DataConsumer consumer = newPartitionConsumer(consumePiperLocation);
                partitionConsumers.put(consumePiperLocation, consumer);
            }
        }

        if(decreaseConsumePipers != null && !decreaseConsumePipers.isEmpty()){
            for (String consumePiperLocation : decreaseConsumePipers){
                DataConsumer consumer = partitionConsumers.get(consumePiperLocation);
                consumer.stop();
                partitionConsumers.remove(consumePiperLocation);
            }
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
     * 开启
     */
    public void start(){
        for(DataConsumer partitionConsumer : partitionConsumers.values()){
            partitionConsumer.start();
        }
    }

    /**
     * 关闭
     */
    public void destroy(){
        for(DataConsumer partitionConsumer : partitionConsumers.values()){
            partitionConsumer.stop();
        }
    }

}