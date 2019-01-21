package com.zq.sword.array.mq.jade.coordinator.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 命名消费者
 * @author: zhouqi1
 * @create: 2019-01-18 10:29
 **/
@Data
@ToString
@NoArgsConstructor
public class NameConsumer {

    /**
     * consumer id
     */
    private long id;

    /**
     * group
     */
    private String group;

    /**
     * 消费的topic
     */
    private String[] topics;

    public NameConsumer(long id, String group, String... topics) {
        this.id = id;
        this.group = group;
        this.topics = topics;
    }
}
