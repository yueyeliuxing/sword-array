package com.zq.sword.array.mq.jade.consumer;

import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.id.SnowFlakeIdGenerator;
import com.zq.sword.array.mq.jade.coordinator.data.NameConsumeAllocator;
import com.zq.sword.array.mq.jade.coordinator.data.NameConsumer;
import com.zq.sword.array.mq.jade.coordinator.NameCoordinator;
import com.zq.sword.array.mq.jade.coordinator.data.NameDuplicatePartition;
import com.zq.sword.array.tasks.AbstractThreadActuator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import static com.zq.sword.array.common.event.HotspotEventType.*;

/**
 * @program: sword-array
 * @description: 抽象  消费者 -> 分片 分配器
 * @author: zhouqi1
 * @create: 2019-01-18 13:58
 **/
public class DefaultConsumeAllocator extends AbstractThreadActuator implements ConsumeAllocator {

    private Logger logger = LoggerFactory.getLogger(DefaultConsumeAllocator.class);

    private long id;

    /**
     * 消费者组
     */
    private String group;

    /**
     * 协调的topic
     */
    private String topic;

    /**
     * 命名服务
     */
    private NameCoordinator coordinator;

    /**
     * 消费者
     */
    private List<NameConsumer> consumers;

    /**
     * 分片
     */
    private List<NameDuplicatePartition> partitions;

    private boolean isCanRegister = true;

    private CountDownLatch latch = new CountDownLatch(1);

    public DefaultConsumeAllocator(NameCoordinator coordinator) {
        this.coordinator = coordinator;
        this.id = createConsumeAllocaterId();
        this.consumers = new ArrayList<>();
        this.partitions = new ArrayList<>();
    }

    protected long createConsumeAllocaterId(){
        return new SnowFlakeIdGenerator().nextId();
    }

    @Override
    public void group(String group) {
        this.group = group;
    }

    @Override
    public void topic(String topic) {
        this.topic = topic;
    }

    @Override
    public void run() {
        while (!isClose && !Thread.currentThread().isInterrupted()){
            try{
                NameConsumeAllocator consumeAllocator = new NameConsumeAllocator(id, group, topic);
                while(!coordinator.registerConsumeAllocator(consumeAllocator, (dataEvent)->{
                    HotspotEventType type = dataEvent.getType();
                    if(HotspotEventType.CONSUME_ALLOCATOR_NODE_DEL.equals(type)){
                        isCanRegister = true;
                        latch.countDown();
                    }
                })){
                    latch.await();
                    if (isCanRegister) {
                        logger.info("监听到分配器临时节点消失，延迟5s启动");
                        Thread.sleep(5000);
                    }
                }
                logger.info("分配器：{}注册成功", id);

                List<NameConsumer> nameConsumers = coordinator.gainConsumers(group, dataEvent -> {
                    if(CONSUMER_NODE_CHANGE.equals(dataEvent.getType())){
                        this.consumers.clear();
                        this.consumers.addAll(dataEvent.getData());
                        reallocateConsumePartition();
                    }
                });
                if(nameConsumers != null && !nameConsumers.isEmpty()){
                    this.consumers.addAll(nameConsumers);
                }

                List<NameDuplicatePartition> nameDuplicatePartitions = coordinator.gainDuplicatePartition(topic, dataEvent -> {
                    if(PARTITION_NODE_CHANGE.equals(dataEvent.getType())){
                        this.partitions.clear();
                        this.partitions.addAll(dataEvent.getData());
                        reallocateConsumePartition();
                    }
                });
                if(nameDuplicatePartitions != null && !nameDuplicatePartitions.isEmpty()){
                    this.partitions.addAll(nameDuplicatePartitions);
                }
                reallocateConsumePartition();
            }catch (Exception e){
                logger.error("分配器出现异常", e);
                stop();
            }

        }
    }

    /**
     * 重新分配消费的分片信息
     */
    private void reallocateConsumePartition(){
        List<NameConsumer> consumers = this.consumers;
        List<NameDuplicatePartition> partitions = this.partitions;

        int consumerSize = consumers.size();
        int partitionSize = partitions.size();

        if(consumerSize == 0 || partitionSize == 0){
            return;
        }
        Map<Integer, List<NameDuplicatePartition>> partitionsOfConsumeIndexs = new HashMap<>();
        for (int i = 0; i < consumerSize; i++) {
            NameDuplicatePartition partition = partitions.get(i);
            putInto(partitionsOfConsumeIndexs, i, partition);
            if(i == consumerSize -1){
                for(int j = i + 1; j < partitionSize; j++){
                    partition = partitions.get(j);
                    putInto(partitionsOfConsumeIndexs, ThreadLocalRandom.current().nextInt(0, consumerSize-1), partition);
                }
            }else if(i == partitionSize - 1){
                break;
            }
        }
        Map<NameConsumer, List<NameDuplicatePartition>> consumeOfPartitions = new HashMap<>();
        partitionsOfConsumeIndexs.forEach((consumeIndex, parts)->{
            consumeOfPartitions.put(consumers.get(consumeIndex), parts);
        });

        //更改消费者对应的消费分片信息
        coordinator.editConsumePartitions(topic, group, consumeOfPartitions);

    }

    private void putInto(Map<Integer, List<NameDuplicatePartition>> partitionsOfConsumeIndexs, int consumerIndex, NameDuplicatePartition partition) {
        List<NameDuplicatePartition> duplicatePartitions = partitionsOfConsumeIndexs.get(consumerIndex);
        if(duplicatePartitions == null){
            duplicatePartitions = new ArrayList<>();
            partitionsOfConsumeIndexs.put(consumerIndex, duplicatePartitions);
        }
        duplicatePartitions.add(partition);
    }
}
