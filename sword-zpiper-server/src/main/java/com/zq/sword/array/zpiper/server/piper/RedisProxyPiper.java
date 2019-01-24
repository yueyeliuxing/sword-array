package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.mq.jade.consumer.ConsumeStatus;
import com.zq.sword.array.mq.jade.consumer.Consumer;
import com.zq.sword.array.mq.jade.consumer.MessageListener;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;
import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.mq.jade.producer.Producer;
import com.zq.sword.array.zpiper.server.piper.cluster.PiperCluster;
import com.zq.sword.array.zpiper.server.piper.cluster.ZkPiperCluster;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperType;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zq.sword.array.zpiper.server.piper.cluster.util.ZkClusterNodePathBuilder.ZK_SWORD_OTHER_PROXY_UNITS;

/**
 * @program: sword-array
 * @description: 代理piper
 * @author: zhouqi1
 * @create: 2019-01-24 11:44
 **/
public class RedisProxyPiper extends AbstractPiper implements Piper {

    private Logger logger = LoggerFactory.getLogger(RedisProxyPiper.class);

    private Consumer consumer;

    private Producer producer;

    private Map<String, PiperCluster> proxyPiperClusters;

    private List<NameCoordinator> proxyCoordinators;

    public RedisProxyPiper(PiperConfig config) {
        super(config);

        this.proxyPiperClusters = new HashMap<>();
        this.proxyCoordinators = new ArrayList<>();
        List<String> otherDcZkLocations = config.otherDcZkLocations();
        if(otherDcZkLocations != null && !otherDcZkLocations.isEmpty()){
            for(String otherDcZkLocation : otherDcZkLocations){
                String[] params = otherDcZkLocation.split(":");
                this.proxyPiperClusters.put(params[0], new ZkPiperCluster(params[1]));
                this.proxyCoordinators.add(new ZkNameCoordinator(params[1]));
            }
        }




        //创建生产者
        this.producer = createProducer();

        //创建消费者
        this.consumer = createConsumer(id()+"group");
        this.consumer.bindingMessageListener(new MessageWriteBrokerListener());

    }

    @Override
    protected void doStartModule() {
        producer.start();
        consumer.start();

        //注册到其他机房
        if(proxyPiperClusters != null && !proxyPiperClusters.isEmpty()){
            for (String  dc : proxyPiperClusters.keySet()){
                PiperCluster proxyPiperCluster = proxyPiperClusters.get(dc);
                NamePiper namePiper = new NamePiper(this.namePiper.getId(), PiperType.OTHER_DC_UNIT_PROXY_PIPER,dc,ZK_SWORD_OTHER_PROXY_UNITS,String.format("%s-%s", dc,this.namePiper.getUnit()),this.namePiper.getGroup(),this.namePiper.getLocation());
                proxyPiperCluster.register(namePiper, (dataEvent -> {

                }));
            }
        }

        //消息分片注册到其他机房
        if(proxyCoordinators != null && !proxyCoordinators.isEmpty()){
            for (NameCoordinator proxyCoordinator : proxyCoordinators){
                registerBrokerAndPartitions(proxyCoordinator);
            }
        }
    }

    @Override
    protected void doStopModule() {
        producer.stop();
        consumer.stop();
    }

    private class MessageWriteBrokerListener implements MessageListener{

        @Override
        public ConsumeStatus consume(Message message) {
            try{
                producer.sendMsg(message);
            }catch (Exception e){
                logger.error("消费消息失败", e);
                return ConsumeStatus.CONSUME_FAIL;
            }
            return ConsumeStatus.CONSUME_SUCCESS;
        }
    }
}