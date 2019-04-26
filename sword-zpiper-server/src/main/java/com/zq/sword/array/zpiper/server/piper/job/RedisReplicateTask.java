package com.zq.sword.array.zpiper.server.piper.job;


import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandSerializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.replicator.DefaultSlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.SlaveRedisReplicator;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.InterPiperProtocol;
import com.zq.sword.array.zpiper.server.piper.cluster.protocol.dto.LocatedDataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: 复制任务
 * @author: zhouqi1
 * @create: 2019-04-24 17:30
 **/
public class RedisReplicateTask extends AbstractTask implements ReplicateTask {

    private Logger logger = LoggerFactory.getLogger(RedisReplicateTask.class);

    private static final String TASK_NAME = "replicate-task";

    public static final String DATA_GROUP = "commands";

    private JobEnv jobEnv;

    private IdGenerator idGenerator;

    private SlaveRedisReplicator redisReplicator;

    private PartitionSystem partitionSystem;

    private TaskExecutor taskExecutor;

    private List<String> replicatePiperLocations;

    public RedisReplicateTask(JobEnv jobEnv, CycleDisposeHandler<RedisCommand> cycleDisposeHandler, PartitionSystem partitionSystem)  {
        super(TASK_NAME);
        this.jobEnv = jobEnv;

        idGenerator = new SnowFlakeIdGenerator();

        //设置redis 复制器
        redisReplicator = new DefaultSlaveRedisReplicator(jobEnv.getSourceRedis());
        redisReplicator.addCommandInterceptor(new CycleCommandFilterInterceptor(cycleDisposeHandler));
        redisReplicator.addRedisReplicatorListener(new RedisCommandListener());

        this.partitionSystem = partitionSystem;

        this.taskExecutor = new SingleTaskExecutor(3);

        this.replicatePiperLocations = new CopyOnWriteArrayList<>();
        assignReplicatePiperLocations(jobEnv);
    }

    /**
     * 为需要复制的piper赋值
     * @param jobEnv
     */
    private void assignReplicatePiperLocations(JobEnv jobEnv) {
        List<String> replicatePiperLocations = jobEnv.getReplicatePipers(new PiperChangeListener() {
            //增加了复制机器
            @Override
            public void increment(List<String> piperLocations) {
                RedisReplicateTask.this.replicatePiperLocations.addAll(piperLocations);
            }

            @Override
            public void decrease(List<String> piperLocations) {
                RedisReplicateTask.this.replicatePiperLocations.removeAll(piperLocations);
            }
        });
        this.replicatePiperLocations.addAll(replicatePiperLocations);
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
            DataEntry message = new DataEntry();
            message.setSeq(idGenerator.nextId());
            message.setTag(command.getType()+"");
            message.setBody(redisCommandSerializer.serialize(command));
            message.setTimestamp(System.currentTimeMillis());
            handleMessage(message);
        }
    }

    /**
     * 消息处理
     * @param message
     */
    private void handleMessage(DataEntry message) {
        //本机接受数据
        Partition partition = partitionSystem.getOrNewPartition(DATA_GROUP, createPartName());
        partition.append(message);

        //异步发送数据到备份机器上
        if(replicatePiperLocations != null && !replicatePiperLocations.isEmpty()){
            for (String replicatePiperLocation : replicatePiperLocations){
                taskExecutor.execute(()->{
                    InterPiperProtocol.InterPiperClient interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(replicatePiperLocation);
                    interPiperClient.sendMessage(new LocatedDataEntry(partition.group(), partition.name(), message));
                });
            }
        }
    }

    private String createPartName() {
        return String.format("%s-%s", jobEnv.getPiperGroup(), jobEnv.getName());
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
