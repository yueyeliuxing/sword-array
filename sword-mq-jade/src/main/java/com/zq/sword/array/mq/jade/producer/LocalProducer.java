package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.broker.Broker;
import com.zq.sword.array.mq.jade.broker.MultiPartition;
import com.zq.sword.array.mq.jade.broker.Partition;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: sword-array
 * @description: 本地生产者
 * @author: zhouqi1
 * @create: 2019-01-16 17:17
 **/
public class LocalProducer implements Producer {

    private Logger logger = LoggerFactory.getLogger(LocalProducer.class);

    private Broker broker;

    public LocalProducer(Broker broker) {
        this.broker = broker;
    }

    @Override
    public boolean sendMsg(Message message) {
        try{
            String topic = message.getTopic();
            List<Partition> partitions = broker.getPartition(topic);
            if(partitions != null && !partitions.isEmpty()){
                for(Partition partition : partitions){
                    ObjectOutputStream outputStream = partition.openOutputStream();
                    outputStream.writeObject(message);
                    outputStream.close();
                }
            }
        }catch (Exception e){
            logger.error("发送消息失败", e);
            return false;
        }

        return true;
    }
}
