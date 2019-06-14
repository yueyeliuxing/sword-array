package com.zq.sword.array.piper.pipeline;

import com.zq.sword.array.network.rpc.framework.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.RpcClient;
import com.zq.sword.array.rpc.api.piper.ReplicateDataService;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateData;
import com.zq.sword.array.rpc.api.piper.dto.ReplicateDataQuery;
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

        private RpcClient rpcClient;

        private ReplicateDataService replicateDataService;

        public DataConsumer(String jobName, String targetPiperLocation) {
            String[] groupLocations = targetPiperLocation.split("\\|");
            String[] ps = targetPiperLocation.split(":");
            rpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));

            replicateDataService = (ReplicateDataService)rpcClient.getProxy(ReplicateDataService.class);
            this.piperGroup = groupLocations[0];
        }

        @Override
        public void start(){
            rpcClient.start();
            super.start();
        }

        @Override
        public void run() {
            logger.info("消费者开始消费消息");
            while (!isClose && !Thread.currentThread().isInterrupted()) {
                ConsumeData consumeData = inflowTask.execute(piperGroup);
                List<ReplicateData> replicateData = replicateDataService.listReplicateData((ReplicateDataQuery) consumeData.getData());
                outflowListener.outflow(new ConsumeData(ConsumeData.REPLICATE_DATA, replicateData));
            }
        }

        @Override
        public void stop() {
            super.stop();
            rpcClient.close();
        }
    }
}
