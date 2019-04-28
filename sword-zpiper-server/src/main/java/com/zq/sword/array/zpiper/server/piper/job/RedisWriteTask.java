package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.redis.command.CommandMetadata;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.util.RedisConfig;
import com.zq.sword.array.redis.writer.DefaultRedisWriter;
import com.zq.sword.array.redis.writer.RedisWriter;
import com.zq.sword.array.zpiper.server.piper.job.cluster.JobDataBackupCluster;
import com.zq.sword.array.zpiper.server.piper.job.cluster.JobDataConsumeCluster;
import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;
import com.zq.sword.array.zpiper.server.piper.job.processor.WriteTaskBackupProcessor;
import com.zq.sword.array.zpiper.server.piper.job.storage.JobRuntimeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zq.sword.array.zpiper.server.piper.job.cluster.JobDataConsumeCluster.PartitionConsumerBuilder;
import static com.zq.sword.array.zpiper.server.piper.job.cluster.JobDataConsumeCluster.get;

/**
 * @program: sword-array
 * @description: 写入任务
 * @author: zhouqi1
 * @create: 2019-04-24 17:52
 **/
public class RedisWriteTask extends AbstractTask implements WriteTask {

    private Logger logger = LoggerFactory.getLogger(RedisWriteTask.class);

    private static final String TASK_NAME = "write-task";

    private JobEnv jobEnv;

    private JobRuntimeStorage jobRuntimeStorage;

    private RedisWriter redisWriter;

    private JobDataBackupCluster jobDataBackupCluster;

    private JobDataConsumeCluster jobDataConsumeCluster;

    public RedisWriteTask(JobEnv jobEnv, JobRuntimeStorage jobRuntimeStorage, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(TASK_NAME);
        this.jobEnv = jobEnv;
        this.jobRuntimeStorage = jobRuntimeStorage;

        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(new RedisConfig(jobEnv.getSourceRedis()));
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor(cycleDisposeHandler));

        //Job运行时数据本分处理器
        this.jobDataBackupCluster = JobDataBackupCluster.get(jobEnv.getName());
        this.jobDataBackupCluster.setWriteTaskBackupProcessor(new WriteTaskBackupProcessor() {
            @Override
            public void backupConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {

            }
        });

        //Job任务运行 数据消费处理器
        this.jobDataConsumeCluster = get(jobEnv.getName());
        this.jobDataConsumeCluster.setPartitionConsumerBuilder(new PartitionConsumerBuilder() {
            @Override
            public JobDataConsumeCluster.DataConsumer build(String consumePiperLocation) {
                return new ReplicateDataConsumer(consumePiperLocation);
            }
        });
    }

    /**
     * 数据消费者
     */
    public class ReplicateDataConsumer extends JobDataConsumeCluster.DataConsumer {

        private volatile boolean isCanReq = true;

        public ReplicateDataConsumer(String targetPiperLocation) {
            super(jobEnv.getName(), targetPiperLocation);
        }


        @Override
        public void consumeReplicateData(List<ReplicateData> replicateDatas) {
            //消费消息
            logger.info("接收消息->{}", replicateDatas);
            if(replicateDatas != null && !replicateDatas.isEmpty()){
                for (ReplicateData replicateData : replicateDatas){
                    redisWriter.write(new RedisCommandDeserializer().deserialize(replicateData.getData()), metadata -> {
                        if(metadata.getException() != null){
                            logger.error("写入redis出错", metadata.getException());
                        }
                    });

                    ConsumeNextOffset consumeNextOffset = new ConsumeNextOffset(replicateData.getPiperGroup(), replicateData.getJobName(), replicateData.getNextOffset());
                    //更新分片消费信息
                    jobRuntimeStorage.writeConsumeNextOffset(consumeNextOffset);

                    //异步发送数据到备份机器上
                    jobDataBackupCluster.backupConsumeNextOffset(consumeNextOffset);
                }
            }
            //数据消费完 可以继续请求数据了
            isCanReq = true;
        }

        @Override
        public void run() {
            logger.info("消费者开始消费消息");
            while (!isClose && !Thread.currentThread().isInterrupted()) {
                if (isCanReq) {
                    long offset = jobRuntimeStorage.getConsumeNextOffset(jobEnv.getPiperGroup(), jobEnv.getName());
                    logger.info("消费者消费offset->{}, piperGroup->{} jobName->{}", offset, jobEnv.getPiperGroup(), jobEnv.getName());
                    consumeReplicateDataReq(new ReplicateDataReq(jobEnv.getPiperGroup(), jobEnv.getName(), offset, 1));
                    isCanReq = false;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 循环命令添加拦截器
     */
    private class CycleCommandAddInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

        public CycleCommandAddInterceptor(CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
            this.cycleDisposeHandler = cycleDisposeHandler;
        }

        @Override
        public void onAcknowledgment(CommandMetadata metadata) {
            Exception exception = metadata.getException();
            if(exception == null){
                logger.info("命令写入循环缓存->{}", metadata.getCommand());
                cycleDisposeHandler.addCycleData(metadata.getCommand());
            }
        }
    }

    @Override
    public void run() {
        redisWriter.start();
        super.run();
    }

    @Override
    public void stop() {
        super.stop();
        redisWriter.stop();
        jobDataConsumeCluster.close();
    }
}
