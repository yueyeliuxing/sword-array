package com.zq.sword.array.mq.jade.broker;

/**
 * @program: sword-array
 * @description: 抽象分片
 * @author: zhouqi1
 * @create: 2019-04-24 15:21
 **/
public abstract class AbstractPartition implements Partition {

    /**
     * 分片文件前缀
     */
    private static final String PARTITION_FILE_PREFIX = "part-";

    private long id;

    public AbstractPartition(long id) {
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String name() {
        return PARTITION_FILE_PREFIX + "_" + id;
    }
}
