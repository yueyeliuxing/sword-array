package com.zq.sword.array.mq.jade.producer;

import com.zq.sword.array.mq.jade.msg.Message;

import java.io.Closeable;

/**
 * @program: sword-array
 * @description: 只写数据分片
 * @author: zhouqi1
 * @create: 2019-04-18 16:46
 **/
public interface OutputPartition extends Closeable {

    /**
     * 追加消息
     * @param message
     * @return
     */
    long append(Message message);

}
