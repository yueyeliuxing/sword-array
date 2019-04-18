package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;

import java.util.List;

/**
 * @program: sword-array
 * @description: 备份分片资源
 * @author: zhouqi1
 * @create: 2019-01-17 16:57
 **/
public class DuplicateProducePartition extends ProducePartition {

    private TaskExecutor taskExecutor;

    private List<Partition> slaves;

    public DuplicateProducePartition(Partition master, List<Partition> slaves) {
        super(master);
        this.slaves = slaves;
        this.taskExecutor = new SingleTaskExecutor();
    }

    @Override
    public long append(Message message) {
        long offset = super.append(message);
        if (slaves != null && !slaves.isEmpty()) {
            for (Partition slave : slaves) {
                taskExecutor.execute(() -> {
                    slave.append(message);
                });
            }

        }
        return offset;
    }

    @Override
    public void close() {
        super.close();
        if (slaves != null && !slaves.isEmpty()) {
            for (Partition slave : slaves) {
                slave.close();
            }

        }
    }

}
