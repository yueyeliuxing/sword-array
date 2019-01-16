package com.zq.sword.array.data.structure.queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @program: sword-array
 * @description: 原始队列
 * @author: zhouqi1
 * @create: 2019-01-15 13:32
 **/
public class PrimitiveDataQueue<T> extends AbstractDataQueue<T> implements DataQueue<T> {

    public PrimitiveDataQueue(){
        super(new LinkedBlockingQueue<>(Integer.MAX_VALUE));
    }

    @Override
    protected void doPush(T t) {
        queue.offer(t);
    }

    @Override
    protected T doPoll() {
        return queue.poll();
    }
}
