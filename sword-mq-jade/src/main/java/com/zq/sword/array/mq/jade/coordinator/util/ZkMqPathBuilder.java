package com.zq.sword.array.mq.jade.coordinator.util;

import com.zq.sword.array.mq.jade.coordinator.data.NameBroker;
import com.zq.sword.array.mq.jade.coordinator.data.NameConsumeAllocator;
import com.zq.sword.array.mq.jade.coordinator.data.NameConsumer;
import com.zq.sword.array.mq.jade.coordinator.data.NamePartition;

/**
 * @program: sword-array
 * @description: ZK树节点路径帮助类
 * @author: zhouqi1
 * @create: 2018-07-24 15:10
 **/
public class ZkMqPathBuilder {

    public static final String ZK_ROOT = "/p-piper";

    public static final String ZK_MQ_ROOT= ZK_ROOT + "/mq";

    public static final String ZK_MQ_BROKER_IDS = ZK_ROOT + "/brokers/ids";

    public static final String ZK_MQ_BROKER_IDS_ID_FORMAT = ZK_MQ_BROKER_IDS + "/%s";

    public static final String ZK_MQ_TOPICS = ZK_MQ_ROOT + "/topics";

    public static final String ZK_MQ_TOPICS_TOPIC_FORMAT = ZK_MQ_TOPICS + "/%s";

    public static final String ZK_MQ_TOPICS_TOPIC_PARTS_FORMAT = ZK_MQ_TOPICS_TOPIC_FORMAT + "/parts";

    public static final String ZK_MQ_TOPICS_TOPIC_PARTS_ID_FORMAT = ZK_MQ_TOPICS_TOPIC_PARTS_FORMAT + "/%s";

    public static final String ZK_MQ_CUSTOMERS = ZK_ROOT + "/consumers";

    public static final String ZK_MQ_CUSTOMERS_GROUP_FORMAT = ZK_MQ_CUSTOMERS + "/%s";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_IDS = ZK_MQ_CUSTOMERS_GROUP_FORMAT + "/ids";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS = ZK_MQ_CUSTOMERS_GROUP_FORMAT + "/topics";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_IDS_ID_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_IDS + "/%s";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS + "/%s";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_ALLOCATOR_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_FORMAT + "/allocator";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_DETAILED_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_FORMAT + "/detailed";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_PARTS_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_FORMAT + "/parts";

    public static final String ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_PARTS_ID_FORMAT = ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_PARTS_FORMAT + "/%s";

    public static String getMqRealPath(String path) {
        return ZK_MQ_ROOT+path;
    }

    /**
     * IDS 路径
     * @return
     */
    public static String buildBrokerIdsPath(){
        return ZK_MQ_BROKER_IDS;
    }

    /**
     * 获取broker注册地址
     * @param broker
     * @return
     */
    public static String buildBrokerRegisterPath(NameBroker broker){
        return String.format(ZK_MQ_BROKER_IDS_ID_FORMAT, broker.getId());
    }

    /**
     * 获取所有topic 的父节点
     * @return
     */
    public static String buildTopicParentPath(){
        return ZK_MQ_TOPICS;
    }

    /**
     * 获取partition注册地址
     * @param topic
     * @return
     */
    public static String buildPartitionParentPath(String topic){
        return String.format(ZK_MQ_TOPICS_TOPIC_PARTS_FORMAT, topic);
    }

    /**
     * 获取partition注册地址
     * @param partition
     * @return
     */
    public static String buildPartitionRegisterPath(NamePartition partition){
        return String.format(ZK_MQ_TOPICS_TOPIC_PARTS_ID_FORMAT, partition.getTopic(), partition.getId());
    }

    /**
     * 获取consumer注册地址
     * @param consumer
     * @return
     */
    public static String buildConsumerRegisterPath(NameConsumer consumer){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_IDS_ID_FORMAT, consumer.getGroup(), consumer.getId());
    }

    /**
     * 获取consumer注册地址
     * @param group
     * @return
     */
    public static String buildConsumerParentPath(String group){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_IDS, group);
    }

    /**
     * 消费者的父节点
     * @param group
     * @param topic
     * @return
     */
    public static String buildConsumerParentPath(String group, String topic){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_FORMAT, group, topic);
    }

    /**
     * 获取consumer消费清单地址
     * @param consumeAllocator
     * @return
     */
    public static String buildConsumerAllocatorRegisterPath(NameConsumeAllocator consumeAllocator){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_ALLOCATOR_FORMAT, consumeAllocator.getGroup(), consumeAllocator.getTopic());
    }

    /**
     * 获取consumer消费清单地址
     * @param group
     * @param topic
     * @return
     */
    public static String buildConsumerDetailedPath(String group, String topic){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_DETAILED_FORMAT, group, topic);
    }

    /**
     * 获取consumer消费清单地址
     * @param group
     * @param topic
     * @return
     */
    public static String buildConsumePartitionPath(String group, String topic, long partId){
        return String.format(ZK_MQ_CUSTOMERS_GROUP_CUSTOMER_TOPICS_TOPIC_PARTS_ID_FORMAT, group, topic, partId);
    }
}
