package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.msg.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 本地broker 生产者
 * @author: zhouqi1
 * @create: 2019-01-17 16:10
 **/
public class GeneralProducer implements Producer {

    private Logger logger = LoggerFactory.getLogger(GeneralProducer.class);

    private PartitionAlloter partitionAlloter;

    public GeneralProducer(PartitionAlloter partitionAlloter) {
        this.partitionAlloter = partitionAlloter;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean sendMsg(Message message) {
        String topic = message.getTopic();
        ProducePartition producePartition = partitionAlloter.allotPartition(topic);
        if(producePartition != null){
            producePartition.append(message);
            return true;
        }
        return false;
    }
}
