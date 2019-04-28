package com.zq.sword.array.zpiper.server.piper.job;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.replicator.DefaultSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.zpiper.server.piper.job.dto.ReplicateData;
import com.zq.sword.array.zpiper.server.piper.job.storage.JobRuntimeStorage;
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

    private JobContext context;

    private SlaveRedisReplicator redisReplicator;

    private JobRuntimeStorage jobRuntimeStorage;

    public RedisReplicateTask(JobContext context, CycleDisposeHandler<RedisCommand> cycleDisposeHandler)  {
        super(TASK_NAME);
        this.context = context;

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(this.context.getSourceRedis());
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor(cycleDisposeHandler));
        redisReplicator.addRedisReplicatorListener(new RedisCommandListener());

        this.jobRuntimeStorage = context.getJobRuntimeStorage();
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
            long offset = jobRuntimeStorage.writeReplicateData(new ReplicateData(context.getPiperGroup(), context.getName(), data));

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
