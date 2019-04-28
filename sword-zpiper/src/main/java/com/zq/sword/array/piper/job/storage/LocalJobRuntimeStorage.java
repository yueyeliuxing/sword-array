package com.zq.sword.array.piper.job.storage;

import com.zq.sword.array.data.storage.DataEntry;
import com.zq.sword.array.data.storage.DataPartitionSystem;
import com.zq.sword.array.data.storage.Partition;
import com.zq.sword.array.data.storage.PartitionSystem;
import com.zq.sword.array.id.IdGenerator;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.piper.job.dto.ConsumeNextOffset;
import com.zq.sword.array.piper.job.dto.ReplicateData;
import com.zq.sword.array.piper.job.dto.ReplicateDataReq;
import com.zq.sword.array.piper.protocol.processor.ReplicateDataReqProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: Job运行时存储
 * @author: zhouqi1
 * @create: 2019-04-26 16:19
 **/
public class LocalJobRuntimeStorage implements JobRuntimeStorage, ReplicateDataReqProcessor {

    private Logger logger = LoggerFactory.getLogger(LocalJobRuntimeStorage.class);

    private static final String DATA_GROUP = "commands";

    private static final String CONSUME_DATA_GROUP = "part-consume";

    private IdGenerator idGenerator;

    /**
     * 分片存储系统
     */
    private PartitionSystem partitionSystem;

    public LocalJobRuntimeStorage(String jobRuntimeStoragePath) {
        partitionSystem = DataPartitionSystem.get(jobRuntimeStoragePath);
        idGenerator = new SnowFlakeIdGenerator();
    }

    /**
     * 写入复制的redis指令
     * @param replicateData
     */
    @Override
    public long writeReplicateData(ReplicateData replicateData){
        DataEntry message = new DataEntry();
        message.setSeq(idGenerator.nextId());
        message.setTag(DATA_GROUP);
        message.setBody(replicateData.getData());
        message.setTimestamp(System.currentTimeMillis());
        //本机接受数据
        Partition partition = partitionSystem.getOrNewPartition(getJobDataPartGroup(replicateData.getJobName()),
                buildPartName(replicateData.getPiperGroup(), replicateData.getJobName()));
        return partition.append(message);
    }

    /**
     * 读取复制数据
     * @param req
     * @return
     */
    @Override
    public List<ReplicateData> readReplicateData(ReplicateDataReq req){
        List<ReplicateData> replicateData = new ArrayList<>();
        Partition partition = partitionSystem.getPartition(getJobDataPartGroup(req.getJobName()),
                buildPartName(req.getPiperGroup(), req.getJobName()));
        if(partition == null){
            logger.warn("查询的分片不存在, group:{} name:{}", req.getPiperGroup(), req.getJobName());
            return null;
        }
        long nextOffset = req.getOffset();
        List<DataEntry> dataEntries = partition.orderGet(req.getOffset(), req.getReqSize());
        if(dataEntries != null && !dataEntries.isEmpty()){
            for(DataEntry dataEntry : dataEntries){
                nextOffset += dataEntry.length();
                replicateData.add(new ReplicateData(req.getPiperGroup(), req.getJobName(), dataEntry.getBody(), nextOffset));
            }
        }
        return replicateData;
    }

    /**
     * 获取指定Job的数据Group
     * @return
     */
    private String getJobDataPartGroup(String jobName){
        return String.format("%s-%s", jobName, DATA_GROUP);
    }

    /**
     * 获取指定Job的数据Group
     * @return
     */
    private String getJobDataConsumeGroup(String jobName){
        return String.format("%s-%s", jobName, DATA_GROUP);
    }

    /**
     *  构建分片名称
     * @return
     */
    private String buildPartName(String piperGroup, String jobName) {
        return String.format("%s-%s", piperGroup, jobName);
    }

    /**
     * 获取消费的下一个offset
     * @param piperGroup
     * @param jobName
     * @return
     */
    @Override
    public long getConsumeNextOffset(String piperGroup, String jobName){
        long offset = 0;
        Partition partition = partitionSystem.getOrNewPartition(getJobDataConsumeGroup(jobName), buildPartName(piperGroup, jobName));
        DataEntry dataEntry = partition.get(0);
        if(dataEntry != null){
            offset = Long.parseLong(new String(dataEntry.getBody()));
        }
        return offset;
    }

    /**
     * 写入要消费下一个的offset
     * @param consumeNextOffset
     */
    @Override
    public void writeConsumedNextOffset(ConsumeNextOffset consumeNextOffset){
        //更新分片消费信息
        Partition partition = partitionSystem.getOrNewPartition(getJobDataConsumeGroup(consumeNextOffset.getJobName()),
                buildPartName(consumeNextOffset.getPiperGroup(), consumeNextOffset.getJobName()));
        DataEntry dataEntry = partition.get(0);
        if(dataEntry == null){
            dataEntry = new DataEntry();
            dataEntry.setTag(CONSUME_DATA_GROUP);
        }
        dataEntry.setSeq(idGenerator.nextId());
        dataEntry.setBody(dataEntry.getBody() == null ? "0".getBytes() :
                String.valueOf(consumeNextOffset.getNextOffset()).getBytes());
        dataEntry.setTimestamp(System.currentTimeMillis());
        partition.append(dataEntry);
    }

    @Override
    public List<ReplicateData> handleReplicateDataReq(ReplicateDataReq req) {
        return readReplicateData(req);
    }

    @Override
    public void handleReplicateData(ReplicateData replicateData) {
        writeReplicateData(replicateData);
    }

    @Override
    public void handleConsumeNextOffset(ConsumeNextOffset consumeNextOffset) {
        writeConsumedNextOffset(consumeNextOffset);
    }
}
