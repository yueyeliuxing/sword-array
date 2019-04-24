package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
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
public class LocalPartition extends AbstractPartition implements Partition {

    private Logger logger = LoggerFactory.getLogger(LocalPartition.class);

    private Broker broker;

    /**
     * 数据存储
     */
    private OffsetSeqFile<Message> dataFile;

    private volatile boolean isClose = false;

    public LocalPartition(Broker broker, long id) {
        super(id);
        this.broker = broker;
        this.dataFile = OffsetBlockFile.get(broker.getResourceLocation() + File.separator + name(), Message.class);
    }

    public LocalPartition(Broker broker, File partitionFile) {
        this(broker, Long.parseLong(partitionFile.getName().split("_")[1]));
    }

    @Override
    public long append(Message message) {
        return dataFile.write(message);
    }

    @Override
    public Message search(long offset) {
        return dataFile.read(offset);
    }

    @Override
    public List<Message> orderSearch(long offset, int num) {
        return dataFile.read(offset, num);
    }

    @Override
    public boolean isClose() {
        return isClose;
    }

    @Override
    public void close() {
        isClose = true;
    }
}
