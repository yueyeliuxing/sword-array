package com.zq.sword.array.zpiper.server.piper.job;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.replicator.DefaultSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.zpiper.server.piper.cluster.JobDataBackupCluster;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateDataId;
import com.zq.sword.array.zpiper.server.piper.job.processor.ReplicateTaskBackupProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 复制任务
 * @author: zhouqi1
 * @create: 2019-04-24 17:30
 **/
public class RedisReplicateTask extends AbstractTask implements ReplicateTask {

    private Logger logger = LoggerFactory.getLogger(RedisReplicateTask.class);

    private static final String TASK_NAME = "replicate-task";

    private JobEnv jobEnv;

    private SlaveRedisReplicator redisReplicator;

    private JobRuntimeStorage jobRuntimeStorage;

    private JobDataBackupCluster jobDataBackupCluster;

    public RedisReplicateTask(JobEnv jobEnv, CycleDisposeHandler<RedisCommand> cycleDisposeHandler, JobRuntimeStorage jobRuntimeStorage)  {
        super(TASK_NAME);
        this.jobEnv = jobEnv;

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(this.jobEnv.getSourceRedis());
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor(cycleDisposeHandler));
        redisReplicator.addRedisReplicatorListener(new RedisCommandListener());

        this.jobRuntimeStorage = jobRuntimeStorage;

        this.jobDataBackupCluster = JobDataBackupCluster.get(jobEnv.getName());
        this.jobDataBackupCluster.setReplicateTaskBackupProcessor(new ReplicateTaskBackupProcessor() {

            @Override
            public void backupReplicateData(ReplicateDataId replicateDataId) {

            }
        });
    }

    /**
     * 循环命令过滤拦截器
     */
    private class CycleCommandFilterInterceptor extends AbstractCommandInterceptor implements CommandInterceptor {

        private CycleDisposeHandler<RedisCommand> cycleDisposeHandler;

        public CycleCommandFilterInterceptor(CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
            this.cycleDisposeHandler = cycleDisposeHandler;
        }

        @Override
        public RedisCommand interceptor(RedisCommand command) {
            logger.info("命令比对->{}", command);
            if(cycleDisposeHandler.isCycleData(command)){
                logger.info("命令存在循环缓存->{}", command);
                return null;
            }
            return command;
        }
    }

    /**
     * redis 命令监听器
     */
    private class RedisCommandListener implements RedisReplicatorListener {

        private RedisCommandSerializer redisCommandSerializer = new RedisCommandSerializer();

        @Override
        public void receive(RedisCommand command) {
            logger.info("接收到命令，时间：{}", System.currentTimeMillis());
            byte[] data = redisCommandSerializer.serialize(command);
            long offset = jobRuntimeStorage.writeReplicateData(new ReplicateData(jobEnv.getPiperGroup(), jobEnv.getName(), data));

            //异步发送数据到备份机器上
            jobDataBackupCluster.backupReplicateData(new ReplicateData(jobEnv.getPiperGroup(), jobEnv.getName(), offset, data));
        }
    }

    @Override
    public void run() {
        redisReplicator.start();
        super.run();
    }

    @Override
    public void stop() {
        redisReplicator.stop();
        super.stop();
    }
}
