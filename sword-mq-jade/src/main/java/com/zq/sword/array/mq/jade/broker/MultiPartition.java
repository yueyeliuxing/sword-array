package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.storage.OffsetFileStorageEngine;
import com.zq.sword.array.stream.io.storage.OffsetStorageEngine;
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
public class MultiPartition implements Partition {

    private Logger logger = LoggerFactory.getLogger(MultiPartition.class);

    /**
     * 分片文件前缀
     */
    private static final String PARTITION_FILE_PREFIX = "part-";

    private Broker broker;

    private long id;

    private String tag;

    private String name;

    private String topic;

    private File partitionFile;

    /**
     * 数据存储
     */
    private OffsetStorageEngine<Message> offsetStorageEngine;

    private volatile boolean isClose = false;

    public MultiPartition(Broker broker, String topic, String tag, long id) {
        this.broker = broker;
        this.topic = topic;
        this.tag = tag;
        this.id = id;
        this.name = PARTITION_FILE_PREFIX + tag + "-" + id;
        this.partitionFile = new File(broker.getResourceLocation() + File.separator + topic + File.separator + name);
        this.offsetStorageEngine = new OffsetFileStorageEngine(partitionFile.getPath(), Message.class);
    }

    public MultiPartition(Broker broker, File partitionFile) {
        this.broker = broker;
        this.partitionFile = partitionFile;
        this.name = partitionFile.getName();
        String[] params = partitionFile.getName().split("-");
        this.topic = partitionFile.getParentFile().getName();
        this.tag = params[1];
        this.id = Long.parseLong(params[2]);
        this.offsetStorageEngine = new OffsetFileStorageEngine(partitionFile.getPath(), Message.class);
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String tag() {
        return tag;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String path() {
        return partitionFile.getPath();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public long append(Message message) {
        return offsetStorageEngine.append(message);
    }

    @Override
    public Message search(long offset) {
        return offsetStorageEngine.search(offset);
    }

    @Override
    public List<Message> orderSearch(long offset, int num) {
        return offsetStorageEngine.search(offset, num);
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
