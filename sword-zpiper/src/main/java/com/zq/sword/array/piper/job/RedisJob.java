package com.zq.sword.array.piper.job;

import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ConsumeNextOffset;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateData;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataId;
import com.zq.sword.array.network.rpc.protocol.dto.piper.data.ReplicateDataReq;
import com.zq.sword.array.piper.pipeline.*;
import com.zq.sword.array.piper.storage.RedisDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-24 16:31
 **/
public class RedisJob extends AbstractJob implements Job {

    private Logger logger = LoggerFactory.getLogger(RedisJob.class);

    /**
     * Job上下文
     */
    private JobContext context;

    /**
     * Redis 管道
     */
    private Pipeline<byte[]> redisPipeline;

    /**
     * Redis 数据存储
     */
    private RedisDataStorage redisDataStorage;

    /**
     * 数据消费通道
     */
    private AutoInflowPipeline<ConsumeData> consumePipeline;

    /**
     * 数据备份管道
     */
    private RefreshPipeline<BackupData> backupPipeline;


    public RedisJob(JobContext jobContext, RedisDataStorage redisDataStorage, JobMonitor jobMonitor) {
        super(jobContext.getName(), jobMonitor);

        context = jobContext;

        this.redisDataStorage = redisDataStorage;

        /**
         * Redis 输入输出
         */
        redisPipeline = new RedisPipeline(jobContext.getSourceRedis());
        redisPipeline.outflow((data)->{
            //写入本地存储
            ReplicateData replicateData = new ReplicateData(context.getPiperGroup(), context.getName(), data);
            long offset = redisDataStorage.writeReplicateData(replicateData);
            replicateData.setOffset(offset);
            //备份
            backupPipeline.inflow(new BackupData(BackupData.REPLICATE_DATA, replicateData));
        });


        //创建Job运行时数据消费处理器
        consumePipeline = new ConsumePipeline(jobContext.getName(), jobContext.getConsumePipers());
        consumePipeline.inflow((param)->{
            String piperGroup = (String)param;
            logger.info("消费者开始消费消息");
            long offset = redisDataStorage.getConsumeNextOffset(piperGroup, context.getName());
            logger.info("消费者消费offset->{}, piperGroup->{} jobName->{}", offset, piperGroup, context.getName());
            return new ConsumeData(ConsumeData.REPLICATE_DATA_REQ, new ReplicateDataReq(piperGroup, context.getName(), offset, 1));
        });
        consumePipeline.outflow((data)->{
            List<ReplicateData> replicateDatas = (List<ReplicateData>) data.getData();
            //消费消息
            logger.info("接收消息->{}", replicateDatas);
            if(replicateDatas != null && !replicateDatas.isEmpty()){
                for (ReplicateData replicateData : replicateDatas){

                    //写入Redis管道
                    redisPipeline.inflow(replicateData.getData());

                    ConsumeNextOffset consumeNextOffset = new ConsumeNextOffset(replicateData.getPiperGroup(), replicateData.getJobName(), replicateData.getNextOffset());
                    //更新分片消费信息
                    redisDataStorage.writeConsumedNextOffset(consumeNextOffset);

                    //备份到其他piper上
                    backupPipeline.inflow(new BackupData(BackupData.CONSUME_DATA, consumeNextOffset));
                }
            }
        });

        /**
         * 数据备份管道
         */
        this.backupPipeline = new BackupPipeline(jobContext.getName(), jobContext.getBackupPipers());
        this.backupPipeline.outflow((data)->{
            switch (data.getType()){
                case BackupData.REPLICATE_DATA_RESP:
                    ReplicateDataId replicateDataId = (ReplicateDataId)data.getData();
                    break;
                case BackupData.CONSUME_DATA_RESP:
                    ConsumeNextOffset consumeNextOffset = (ConsumeNextOffset)data.getData();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 刷新任务消费piper
     * @param incrementConsumePipers
     * @param decreaseConsumePipers
     */
    public void flushJobConsumePipers(List<String> incrementConsumePipers, List<String> decreaseConsumePipers){
        PipeConfig pipeConfig = new PipeConfig();
        pipeConfig.put("incrementConsumePipers", incrementConsumePipers);
        pipeConfig.put("decreaseConsumePipers", decreaseConsumePipers);
        consumePipeline.refresh(pipeConfig);
    }

    /**
     * 刷新任务备份piper
     * @param incrementBackupPipers
     * @param decreaseBackupPipers
     */
    public void flushJobBackupPipers(List<String> incrementBackupPipers, List<String> decreaseBackupPipers){
        PipeConfig pipeConfig = new PipeConfig();
        pipeConfig.put("incrementBackupPipers", incrementBackupPipers);
        pipeConfig.put("decreaseBackupPipers", decreaseBackupPipers);
        backupPipeline.refresh(pipeConfig);
    }


    /**
     * 任务启动
     */
    @Override
    public void start() {
        redisPipeline.open();
        consumePipeline.open();
        backupPipeline.open();
    }

    /**
     * 重启ReplicateTask
     */
    @Override
    public void restart(){
        destroy();
        start();
    }
    /**
     * 任务销毁
     */
    @Override
    public void destroy() {
        redisPipeline.close();
        consumePipeline.close();
        backupPipeline.close();
    }

}
