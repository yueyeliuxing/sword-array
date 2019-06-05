package com.zq.sword.array.piper.pipeline;

import com.zq.sword.array.network.rpc.protocol.InterPiperProtocol;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;
import com.zq.sword.array.network.rpc.protocol.processor.ConsumeDataResultProcessor;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.tasks.Actuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 消费管道
 * @author: zhouqi1
 * @create: 2019-06-04 15:37
 **/
public class ConsumePipeline implements AutoInflowPipeline<ConsumeData> {

    private String jobName;

    private Map<String, DataConsumer> partitionConsumers;

    private OutflowListener<ConsumeData> outflowListener;

    private InflowTask<ConsumeData> inflowTask;

    public ConsumePipeline(String jobName, List<String> consumePipers) {
        this.jobName = jobName;
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
                DataConsumer consumer = new DataConsumer(jobName, consumePiperLocation);
                partitionConsumers.put(consumePiperLocation, consumer);
            }
        }
    }

    @Override
    public void refresh(PipeConfig config) {
        List<String> incrementConsumePipers = (List<String>)config.get("incrementConsumePipers");
        if(incrementConsumePipers != null && !incrementConsumePipers.isEmpty()){
            for (String consumePiperLocation : incrementConsumePipers){
                DataConsumer consumer =  new DataConsumer(jobName, consumePiperLocation);
                partitionConsumers.put(consumePiperLocation, consumer);
            }
        }
        List<String> decreaseConsumePipers = (List<String>)config.get("decreaseConsumePipers");
        if(decreaseConsumePipers != null && !decreaseConsumePipers.isEmpty()){
            for (String consumePiperLocation : decreaseConsumePipers){
                DataConsumer consumer = partitionConsumers.get(consumePiperLocation);
                consumer.stop();
                partitionConsumers.remove(consumePiperLocation);
            }
        }
    }

    @Override
    public void open() {
        callbackDataConsumer((consumer)->consumer.start());
    }

    @Override
    public void outflow(OutflowListener<ConsumeData> outflowListener) {
        this.outflowListener = outflowListener;
    }

    @Override
    public void inflow(ConsumeData data) {

    }

    @Override
    public void inflow(InflowTask inflowTask) {
        this.inflowTask = inflowTask;
    }

    @Override
    public void close() {
        callbackDataConsumer((consumer)->consumer.stop());
    }

    /**
     * 回调处理
     * @param callback
     */
    private void callbackDataConsumer(Callback callback){
        //异步发送数据到备份机器上
        if(partitionConsumers != null && !partitionConsumers.isEmpty()){
            for (DataConsumer consumer : partitionConsumers.values()){
                callback.call(consumer);
            }
        }
    }


    /**
     * 回调
     */
    private interface Callback {

        void call(DataConsumer consumer);
    }


    /**
     * 消息消费者
     */
    private class DataConsumer extends AbstractThreadActuator implements Actuator {

        private Logger logger = LoggerFactory.getLogger(DataConsumer.class);

        private String piperGroup;

        private InterPiperProtocol.InterPiperClient interPiperClient;

        private volatile boolean isCanReq = true;

        public DataConsumer(String jobName, String targetPiperLocation) {
            String[] groupLocations = targetPiperLocation.split("\\|");
            this.interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(InterPiperProtocol.InterPiperClient.CONSUME_TYPE, jobName, groupLocations[1]);
            this.interPiperClient.setConsumeDataResultProcessor(new ConsumeDataResultProcessor() {
                @Override
                public void handleReplicateData(List<ReplicateData> replicateData) {
                    outflowListener.outflow(new ConsumeData(ConsumeData.REPLICATE_DATA, replicateData));
                    //数据消费完 可以继续请求数据了
                    isCanReq = true;
                }
            });
            this.piperGroup = groupLocations[0];
        }

        @Override
        public void start(){
            interPiperClient.connect();
            super.start();
        }

        @Override
        public void run() {
            logger.info("消费者开始消费消息");
            while (!isClose && !Thread.currentThread().isInterrupted()) {
                if (isCanReq) {
                    ConsumeData consumeData = inflowTask.execute(piperGroup);
                    interPiperClient.sendReplicateDataReq((ReplicateDataReq) consumeData.getData());
                    isCanReq = false;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            interPiperClient.disconnect();
        }
    }
}
