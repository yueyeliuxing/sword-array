package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.consumer.DefaultConsumeDispatcher;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperType;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: vera
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class RedisXPiper extends RedisPiper {

    private Logger logger = LoggerFactory.getLogger(RedisXPiper.class);

    private List<Consumer> otherConsumers;

    public RedisXPiper(PiperConfig config) {
        super(config);

        this.otherConsumers = new ArrayList<>();
        List<String> otherDcZkLocations = config.otherDcZkLocations();
        if(otherDcZkLocations != null && !otherDcZkLocations.isEmpty()){
            for(String otherDcZkLocation : otherDcZkLocations){
                String[] params = otherDcZkLocation.split("\\|");
                Consumer consumer =  DefaultConsumeDispatcher.createDispatcher(params[1])
                        .createDefaultConsumer(new String[]{namePiper.getGroup()}, id()+"",
                                (partition -> PiperType.PROXY.name().equalsIgnoreCase(partition.getTag())));
                consumer.bindingMessageListener(new ReceiveMessageListener());
                this.otherConsumers.add(consumer);
            }
        }
    }

    @Override
    protected void doStartModule() {
        if(this.otherConsumers != null && !this.otherConsumers.isEmpty()){
            for(Consumer consumer : this.otherConsumers){
                consumer.start();
            }
        }
        super.doStartModule();
    }

    @Override
    protected void doStopModule() {
        if(this.otherConsumers != null && !this.otherConsumers.isEmpty()){
            for(Consumer consumer : this.otherConsumers){
                consumer.stop();
            }
        }
        super.doStopModule();
    }

}
