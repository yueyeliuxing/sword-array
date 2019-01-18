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
     * 消费者组
     */
    protected String group;

    /**
     * 监听的topic
     */
    protected String[] topics;

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
    public void group(String group) {
        this.group = group;
    }

    @Override
    public void listenTopics(String... topics) {
        this.topics = topics;
    }


    @Override
    public void start() {
        if(topics == null || topics.length == 0){
            throw new NullPointerException("topics is null");
        }

        if(group == null){
            throw  new NullPointerException("group is null");
        }
        //创建启动对应的分配器
        createAndStartConsumeAllocator(topics, group);
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
    public void stop() {
        for(Map<String, ConsumeAllocator> groupConsumeAllocators :  consumeAllocators.values()){
            for(ConsumeAllocator consumeAllocator : groupConsumeAllocators.values()){
                consumeAllocator.stop();
            }
        }
    }
}
