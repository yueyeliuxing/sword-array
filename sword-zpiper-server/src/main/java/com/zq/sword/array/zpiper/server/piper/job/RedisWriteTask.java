package com.zq.sword.array.zpiper.server.piper.job;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.redis.command.CommandMetadata;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.command.RedisCommandDeserializer;
import com.zq.sword.array.redis.handler.CycleDisposeHandler;
import com.zq.sword.array.redis.interceptor.AbstractCommandInterceptor;
import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.util.RedisConfig;
import com.zq.sword.array.redis.writer.DefaultRedisWriter;
import com.zq.sword.array.redis.writer.RedisWriter;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import com.zq.sword.array.tasks.Actuator;
import com.zq.sword.array.zpiper.server.piper.job.command.JobCommand;
import com.zq.sword.array.zpiper.server.piper.protocol.InterPiperProtocol;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.DataEntryReq;
import com.zq.sword.array.zpiper.server.piper.protocol.dto.LocatedDataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 写入任务
 * @author: zhouqi1
 * @create: 2019-04-24 17:52
 **/
public class RedisWriteTask extends AbstractTask implements WriteTask {

    private Logger logger = LoggerFactory.getLogger(RedisWriteTask.class);

    private static final String TASK_NAME = "write-task";
    private static final String CONSUME_DATA_GROUP = "part-consume";

    private IdGenerator idGenerator;

    private JobCommand jobCommand;

    private PartitionSystem partitionSystem;

    private RedisWriter redisWriter;

    private List<PartitionConsumer> partitionConsumers;

    private volatile boolean isCanReq = true;

    public RedisWriteTask(JobCommand jobCommand, PartitionSystem partitionSystem, CycleDisposeHandler<RedisCommand> cycleDisposeHandler) {
        super(TASK_NAME);
        this.jobCommand = jobCommand;
        this.partitionSystem = partitionSystem;

        idGenerator = new SnowFlakeIdGenerator();

        //设置redis 写入器
        redisWriter = new DefaultRedisWriter(new RedisConfig(jobCommand.getSourceRedis()));
        redisWriter.addCommandInterceptor(new CycleCommandAddInterceptor(cycleDisposeHandler));

        this.partitionConsumers = new ArrayList<>();
    }

    /**
     * 创建InterPiperClient
     * @param piperLocation
     * @return
     */
    private InterPiperProtocol.InterPiperClient createInterPiperClient(String piperLocation) {
        InterPiperProtocol.InterPiperClient interPiperClient = InterPiperProtocol.getInstance().getOrNewInterPiperClient(piperLocation);
        interPiperClient.addMessageObtainEventListener(new HotspotEventListener<List<LocatedDataEntry>>() {
            @Override
            public void listen(HotspotEvent<List<LocatedDataEntry>> dataEvent) {
                List<LocatedDataEntry> locatedEntrys = dataEvent.getData();
                //消费消息
                logger.info("接收消息->{}", locatedEntrys);
                if(locatedEntrys != null && !locatedEntrys.isEmpty()){
                    for (LocatedDataEntry locatedEntry : locatedEntrys){
                        redisWriter.write(new RedisCommandDeserializer().deserialize(locatedEntry.getEntry().getBody()), metadata -> {
                            if(metadata.getException() != null){
                                logger.error("写入redis出错", metadata.getException());
                            }
                        });

                        //更新分片消费信息
                        Partition partition = partitionSystem.getOrNewPartition(CONSUME_DATA_GROUP, locatedEntry.getPartName());
                        DataEntry dataEntry = partition.get(0);
                        if(dataEntry == null){
                            dataEntry = new DataEntry();
                            dataEntry.setTag(CONSUME_DATA_GROUP);
                        }
                        dataEntry.setSeq(idGenerator.nextId());
                        dataEntry.setBody(dataEntry.getBody() == null ? "0".getBytes() :
                                String.valueOf(Long.parseLong(new String(dataEntry.getBody())) + locatedEntry.getEntry().length()).getBytes());
                        dataEntry.setTimestamp(System.currentTimeMillis());
                        partition.append(dataEntry);
                    }
                }
                //数据消费完 可以继续请求数据了
                isCanReq = true;
            }
        });
        return interPiperClient;
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

        //得到其他pipergroup的Piper远程分片 消费数据
        List<String> targetPiperLocations = jobCommand.getTargetPipers();
        if(targetPiperLocations != null && !targetPiperLocations.isEmpty()){
            for (String targetPiperLocation : targetPiperLocations){
                String[] groupLocations = targetPiperLocation.split("\\|");
                InterPiperProtocol.InterPiperClient interPiperClient = createInterPiperClient(groupLocations[1]);
                PartitionConsumer consumer = new PartitionConsumer(interPiperClient, createPartName(groupLocations[0]));
                consumer.start();
                partitionConsumers.add(consumer);
            }
        }
        super.run();
    }

    /**
     * 相同规则生成制定的partId
     * @param targetGroup
     * @return
     */
    private String createPartName(String targetGroup) {
        return String.format("%s:%s", targetGroup, jobCommand.getName());
    }

    /**
     * 消息消费者
     */
    private class PartitionConsumer extends AbstractThreadActuator implements Actuator {

        private Logger logger = LoggerFactory.getLogger(PartitionConsumer.class);

        private InterPiperProtocol.InterPiperClient interPiperClient;
        private String  partName;

        public PartitionConsumer(InterPiperProtocol.InterPiperClient interPiperClient, String  partName) {
            this.interPiperClient = interPiperClient;
            this.partName = partName;
        }

        @Override
        public void run() {
            //更新分片消费信息
            Partition partition = partitionSystem.getOrNewPartition(CONSUME_DATA_GROUP, partName);
            logger.info("消费者开始消费消息");
            while (!isClose && !Thread.currentThread().isInterrupted()) {

                if(isCanReq){
                    long offset = 0;
                    DataEntry dataEntry = partition.get(0);
                    if(dataEntry != null){
                        offset = Long.parseLong(new String(dataEntry.getBody()));
                    }
                    logger.info("消费者消费offset->{}, partition->{}", offset, partName);
                    interPiperClient.sendMessageReq(new DataEntryReq(RedisReplicateTask.DATA_GROUP, partName, offset, 1));
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
}
