package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.mq.jade.consumer.*;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: 代理piper
 * @author: zhouqi1
 * @create: 2019-01-24 11:44
 **/
public class RedisProxyPiper extends AbstractPiper implements Piper {

    private Logger logger = LoggerFactory.getLogger(RedisProxyPiper.class);

    protected Producer producer;

    private List<Consumer> otherConsumers;

    public RedisProxyPiper(PiperConfig config) {
        super(config);

        //创建生产者
        this.producer = createProducer();

        this.otherConsumers = new ArrayList<>();
        List<String> otherDcZkLocations = config.otherDcZkLocations();
        if(otherDcZkLocations != null && !otherDcZkLocations.isEmpty()){
            for(String otherDcZkLocation : otherDcZkLocations){
                String[] params = otherDcZkLocation.split(":");
                Consumer consumer =  DefaultConsumeDispatcher.createDispatcher(params[1])
                        .createDefaultConsumer(new String[]{namePiper.getGroup()}, id()+"");
                consumer.bindingMessageListener(new ReceiveMessageListener());
                this.otherConsumers.add(consumer);
            }
        }
    }

    /**
     * 接收消息监听器
     */
    private class ReceiveMessageListener implements MessageListener {

        @Override
        public ConsumeStatus consume(Message message) {
            try{
                logger.info("接收消息->{}", message);
                producer.sendMsg(message);
            }catch (Exception e){
                logger.error("接收消息发生异常", e);
                return ConsumeStatus.CONSUME_FAIL;
            }
            return ConsumeStatus.CONSUME_SUCCESS;
        }
    }

    @Override
    protected void doStartModule() {
        this.producer.start();
        if(this.otherConsumers != null && !this.otherConsumers.isEmpty()){
            for(Consumer consumer : this.otherConsumers){
                consumer.start();
            }
        }
    }

    @Override
    protected void doStopModule() {
        this.producer.stop();
        if(this.otherConsumers != null && !this.otherConsumers.isEmpty()){
            for(Consumer consumer : this.otherConsumers){
                consumer.stop();
            }
        }
    }
}
