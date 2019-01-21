package com.zq.sword.array.mq.jade.coordinator.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 消费者分配器
 * @author: zhouqi1
 * @create: 2019-01-18 14:50
 **/
@Data
@ToString
@NoArgsConstructor
public class NameConsumeAllocator {

    /**
     * ID
     */
    private long id;

    /**
     * 消费者组
     */
    private String group;

    /**
     * 协调的topic
     */
    private String topic;

    public NameConsumeAllocator(long id, String group, String topic) {
        this.id = id;
        this.group = group;
        this.topic = topic;
    }
}
