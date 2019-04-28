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
import com.zq.sword.array.zpiper.server.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zq.sword.array.zpiper.server.piper.job.JobDataConsumerPool.PartitionConsumerBuilder;

/**
 * @program: sword-array
 * @description: 写入任务
 * @author: zhouqi1
 * @create: 2019-04-24 17:52
 **/
public class RedisWriteTask extends AbstractTask implements WriteTask {

    private Logger logger = LoggerFactory.getLogger(RedisWriteTask.class);

    private static final String TASK_NAME = "write-task";

    private JobContext jobContext;

    private RedisWriter redisWriter;

    private JobDataConsumerPool jobDataConsumerPool;

    public RedisWriteTask(Job job, JobContext jobContext, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(job, TASK_NAME, jobContext.getJobRuntimeStorage(), jobContext.getTaskMonitor());
        this.jobContext = jobContext;

        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(new RedisConfig(jobContext.getSourceRedis()));
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor(cycleDisposeHandler));

        //创建Job运行时数据消费处理器
        jobDataConsumerPool = new JobDataConsumerPool(jobContext.getConsumePipers());
        jobDataConsumerPool.setPartitionConsumerBuilder(new PartitionConsumerBuilder() {
            @Override
            public JobDataConsumerPool.DataConsumer build(String consumePiperLocation) {
                return new ReplicateDataConsumer(consumePiperLocation);
            }
        });

    }

    @Override
    public void flushJobConsumePipers(List<String> incrementConsumePipers, List<String> decreaseConsumePipers) {
        this.jobDataConsumerPool.handleConsumePiperChange(incrementConsumePipers, decreaseConsumePipers);
    }

    /**
     * 数据消费者
     */
    public class ReplicateDataConsumer extends JobDataConsumerPool.DataConsumer {

        private volatile boolean isCanReq = true;

        public ReplicateDataConsumer(String targetPiperLocation) {
            super(jobContext.getName(), targetPiperLocation);
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
                    jobRuntimeStorage.writeConsumedNextOffset(consumeNextOffset);
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
                    long offset = jobRuntimeStorage.getConsumeNextOffset(jobContext.getPiperGroup(), jobContext.getName());
                    logger.info("消费者消费offset->{}, piperGroup->{} jobName->{}", offset, jobContext.getPiperGroup(), jobContext.getName());
                    consumeReplicateDataReq(new ReplicateDataReq(jobContext.getPiperGroup(), jobContext.getName(), offset, 1));
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
        jobDataConsumerPool.start();
        super.run();
    }

    @Override
    public void stop() {
        super.stop();
        redisWriter.stop();
        jobDataConsumerPool.destroy();
    }
}
