package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.ZkNameCoordinator;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 默认的消费调度器
 * @author: zhouqi1
 * @create: 2019-01-18 20:26
 **/
public class DefaultConsumeDispatcher implements ConsumeDispatcher{

    private static Map<Long, ConsumeDispatcher> DISPATCHERS = new HashMap<>();

    /**
     * 协调器
     */
    private NameCoordinator coordinator;

    /**
     * 消息分配器
     */
    private Map<String, Map<String, ConsumeAllocator>> consumeAllocators;


    static{
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            if(!DISPATCHERS.isEmpty()){
                for(ConsumeDispatcher consumeDispatcher : DISPATCHERS.values()){
                    ((DefaultConsumeDispatcher)consumeDispatcher).destroy();
                }
            }
        }));
    }

    /**
     * 创建分派器
     * @param connectAddr
     * @return
     */
    public static ConsumeDispatcher createDispatcher(String connectAddr){
        return  createDispatcher(new ZkNameCoordinator(connectAddr));

    }
    /**
     * 创建分派器
     * @param coordinator
     * @return
     */
    public static ConsumeDispatcher createDispatcher(NameCoordinator coordinator){
        synchronized (DISPATCHERS){
            ConsumeDispatcher consumeDispatcher = DISPATCHERS.get(coordinator.id());
            if(consumeDispatcher == null){
                consumeDispatcher = new DefaultConsumeDispatcher(coordinator);
                DISPATCHERS.put(coordinator.id(), consumeDispatcher);
            }
            return consumeDispatcher;
        }
    }

    private DefaultConsumeDispatcher(NameCoordinator coordinator) {
        this.coordinator = coordinator;
        this.consumeAllocators = new HashMap<>();
    }


    /**
     * 获取
     * @param topics
     * @param group
     * @return
     */
    private void createAndStartConsumeAllocator(String[] topics, String group){
        for(String topic : topics){
            Map<String, ConsumeAllocator> groupConsumeAllocators = consumeAllocators.get(topic);
            if(groupConsumeAllocators == null || !groupConsumeAllocators.containsKey(group)){
                groupConsumeAllocators = groupConsumeAllocators == null ? new HashMap<>() : groupConsumeAllocators;
                ConsumeAllocator consumeAllocator = new DefaultConsumeAllocator(coordinator);
                consumeAllocator.group(group);
                consumeAllocator.topic(topic);
                consumeAllocator.start();
                groupConsumeAllocators.put(group, consumeAllocator);
                consumeAllocators.put(topic, groupConsumeAllocators);
            }
        }
    }

    /**
     * 销毁消费协调器
     */
    private void destroy() {
        for(Map<String, ConsumeAllocator> groupConsumeAllocators :  consumeAllocators.values()){
            for(ConsumeAllocator consumeAllocator : groupConsumeAllocators.values()){
                consumeAllocator.stop();
            }
        }
    }

    @Override
    public Consumer createDefaultConsumer(String[] topics, String group) {
        createAndStartConsumeAllocator(topics, group);
        return new DefaultConsumer(coordinator, topics, group);
    }

    @Override
    public Consumer createDefaultConsumer(String[] topics, String group, ConsumePartitionFilter partitionFilter) {
        createAndStartConsumeAllocator(topics, group);
        return new DefaultConsumer(coordinator, topics, group, partitionFilter);
    }
}
