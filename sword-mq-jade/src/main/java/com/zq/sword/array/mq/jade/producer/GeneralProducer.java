package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.Resource;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @program: sword-array
 * @description: 本地broker 生产者
 * @author: zhouqi1
 * @create: 2019-01-17 16:10
 **/
public class GeneralProducer implements Producer {

    private Logger logger = LoggerFactory.getLogger(GeneralProducer.class);

    private ProduceDispatcher dispatcher;

    public GeneralProducer(ProduceDispatcher dispatcher) {
        this.dispatcher = dispatcher;
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
        PartitionResource partitionResource = dispatcher.allotPartition(topic);
        if(partitionResource != null){
            try {
                ObjectOutputStream outputStream = partitionResource.openOutputStream();
                outputStream.writeObject(message);
                outputStream.close();
                return true;
            } catch (OutputStreamOpenException e) {
                logger.error("输出流打开失败", e);
            } catch (IOException e) {
                logger.error("输出流写入数据失败", e);
            }
        }

        return false;
    }
}
