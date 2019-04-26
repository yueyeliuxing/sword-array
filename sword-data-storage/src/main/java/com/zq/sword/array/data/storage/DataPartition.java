package com.zq.sword.array.data.storage;

import com.zq.sword.array.stream.io.file.OffsetBlockFile;
import com.zq.sword.array.stream.io.file.OffsetSeqFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @program: sword-array
 * @description: 多路分片
 * @author: zhouqi1
 * @create: 2019-01-16 10:59
 **/
public class DataPartition extends AbstractPartition implements Partition {

    private Logger logger = LoggerFactory.getLogger(DataPartition.class);

    private PartitionSystem system;

    /**
     * 数据存储
     */
    private OffsetSeqFile<DataEntry> dataFile;

    public DataPartition(PartitionSystem system, String group, String name) {
        super(group, name);
        this.system = system;
        this.dataFile = createDataFile(system, group, name);
    }

    /**
     * 创建数据文件
     * @param system
     * @param group
     * @param name
     * @return
     */
    private OffsetBlockFile createDataFile(PartitionSystem system, String group, String name) {
        return OffsetBlockFile.get(system.storePath() + File.separator + group + File.separator + name, DataEntry.class);
    }

    public DataPartition(PartitionSystem broker, File partitionFile) {
        this(broker, partitionFile.getParentFile().getName(), partitionFile.getName());
    }

    @Override
    public long add(long offset, DataEntry entry) {
        return dataFile.write(offset, entry);
    }

    @Override
    public long append(DataEntry entry) {
        return dataFile.write(entry);
    }

    @Override
    public DataEntry get(long offset) {
        return dataFile.read(offset);
    }

    @Override
    public List<DataEntry> orderGet(long offset, int num) {
        return dataFile.read(offset, num);
    }

    @Override
    public Partition copy(String group, String name) {
        dataFile.copyTo(createDataFile(system, group, name));
        return system.createPartition(group, name);
    }

    @Override
    public void destroy() {
        dataFile.delete();
    }
}
