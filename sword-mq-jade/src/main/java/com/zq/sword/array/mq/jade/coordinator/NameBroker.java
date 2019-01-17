package com.zq.sword.array.mq.jade.coordinator;

import lombok.Getter;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 命名broker
 * @author: zhouqi1
 * @create: 2019-01-17 11:19
 **/
@Getter
@ToString
public class NameBroker {

    /**
     * broker id
     */
    private long id;

    /**
     * 定位 host:port
     */
    private String location;

    public NameBroker(long id, String location) {
        this.id = id;
        this.location = location;
    }
}
