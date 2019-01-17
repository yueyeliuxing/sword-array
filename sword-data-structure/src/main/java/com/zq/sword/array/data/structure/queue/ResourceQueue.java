package com.zq.sword.array.data.structure.queue;


import com.zq.sword.array.common.event.HotspotEventEmitter;

import java.util.Queue;

/**
 * @program: sword-array
 * @description: 可存储的对列
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface ResourceQueue<T> extends Queue<T>, HotspotEventEmitter {

    /**
     * 返回队列状态
     * @return
     */
    QueueState state();

}
