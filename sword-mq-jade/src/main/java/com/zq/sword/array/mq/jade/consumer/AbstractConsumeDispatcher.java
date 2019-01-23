package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 抽象 消费调度器
 * @author: zhouqi1
 * @create: 2019-01-18 19:32
 **/
public abstract class AbstractConsumeDispatcher implements ConsumeDispatcher {

    /**
     * 协调器
     */
    protected NameCoordinator coordinator;

    /**
     * 消息分配器
     */
    private Map<String, Map<String, ConsumeAllocator>> consumeAllocators;

    public AbstractConsumeDispatcher(NameCoordinator coordinator) {
        this.coordinator = coordinator;
        this.consumeAllocators = new HashMap<>();
    }

    @Override
    public void start() {

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
                consumeAllocator.start();
                groupConsumeAllocators.put(group, consumeAllocator);
                consumeAllocators.put(topic, groupConsumeAllocators);
            }
        }
    }

    @Override
    public Consumer createConsumer(String[] topics, String group) {
        createAndStartConsumeAllocator(topics, group);
        return doCreateConsumer(topics, group);
    }

    protected abstract Consumer doCreateConsumer(String[] topics, String group);

    @Override
    public void stop() {
        for(Map<String, ConsumeAllocator> groupConsumeAllocators :  consumeAllocators.values()){
            for(ConsumeAllocator consumeAllocator : groupConsumeAllocators.values()){
                consumeAllocator.stop();
            }
        }
    }
}
