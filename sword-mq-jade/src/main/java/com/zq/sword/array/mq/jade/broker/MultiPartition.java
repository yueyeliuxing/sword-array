package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 多路分发分片
 * @author: zhouqi1
 * @create: 2019-04-24 16:08
 **/
public class MultiPartition extends AbstractPartition implements Partition {

    /**
     * 本地分片
     */
    private Partition localPart;

    /**
     * 需要复制的远程分片
     */
    private List<Partition> replicateParts;

    private TaskExecutor taskExecutor;

    public MultiPartition(Broker broker, Long id, List<String> locations) {
        super(id);
        this.localPart = broker.getPartition(id);
        this.replicateParts = new ArrayList<>();
        if(locations != null && !locations.isEmpty()){
            for(String location : locations){
                replicateParts.add(new RpcPartition(broker, id, location));
            }
            taskExecutor = new SingleTaskExecutor(locations.size());
        }
    }

    @Override
    public long append(Message message) {
        long offset = localPart.append(message);
        if(replicateParts != null && !replicateParts.isEmpty()){
            for(Partition partition : replicateParts){
                taskExecutor.execute(()->{
                    partition.append(message);
                });
            }
        }
        return offset;
    }

    @Override
    public Message search(long offset) {
        return localPart.search(offset);
    }

    @Override
    public List<Message> orderSearch(long offset, int num) {
        return localPart.orderSearch(offset, num);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClose() {
        return false;
    }
}
