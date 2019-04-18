package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.msg.Message;

/**
 * @program: sword-array
 * @description: 备份分片资源
 * @author: zhouqi1
 * @create: 2019-01-17 16:57
 **/
public class ProducePartition implements OutputPartition {

    private Partition partition;

    public ProducePartition(Partition partition) {
        this.partition = partition;
    }

    @Override
    public void close() {
        partition.close();
    }

    @Override
    public long append(Message message) {
        return partition.append(message);
    }
}
