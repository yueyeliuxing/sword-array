package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.mq.jade.msg.Message;
import com.zq.sword.array.stream.io.Resource;

/**
 * @program: sword-array
 * @description: 数据块
 * @author: zhouqi1
 * @create: 2019-01-16 10:30
 **/
public interface Partition extends Resource {

    /**
     * id
     * @return
     */
    long id();

    /**
     * 名称
     * @return
     */
    String name();

    /**
     * 路径
     * @return
     */
    String path();

}
