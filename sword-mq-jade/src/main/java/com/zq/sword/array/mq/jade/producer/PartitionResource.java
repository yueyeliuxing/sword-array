package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.stream.io.Resource;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;

/**
 * @program: sword-array
 * @description: 备份分片资源
 * @author: zhouqi1
 * @create: 2019-01-17 16:57
 **/
public class PartitionResource implements Resource {

    private Partition partition;

    public PartitionResource(Partition partition) {
        this.partition = partition;
    }

    @Override
    public ObjectInputStream openInputStream() throws InputStreamOpenException {
        return partition.openInputStream();
    }

    @Override
    public ObjectOutputStream openOutputStream() throws OutputStreamOpenException {
        return partition.openOutputStream();
    }

    @Override
    public void close() {

    }

}
