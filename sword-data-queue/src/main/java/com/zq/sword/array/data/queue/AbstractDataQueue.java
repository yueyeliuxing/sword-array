package com.zq.sword.array.data.queue;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.event.DefaultDataEventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * @program: sword-array
 * @description: 对列抽象
 * @author: zhouqi1
 * @create: 2018-12-25 14:58
 **/
public abstract class AbstractDataQueue<T> extends AbstractQueue<T> implements DataQueue<T> {

    private Logger logger = LoggerFactory.getLogger(AbstractDataQueue.class);

    /***
     * 队列的状态
     */
    protected volatile QueueState state;

    /**
     * 内存队列
     */
    protected Queue<T> queue;

    /**
     * 事件发射器
     */
    protected DefaultDataEventEmitter dataEventEmitter;

    public AbstractDataQueue(Queue<T> queue) {
        this.queue = queue;
        dataEventEmitter = new DefaultDataEventEmitter();
    }

    @Override
    public boolean offer(T t) {
        if(state() != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }

        doPush(t);

        //数据添加通知监听器
        DataEvent<T> dataEvent = new DataEvent<>();
        dataEvent.setType(DataEventType.SWORD_DATA_ADD);
        dataEvent.setData(t);
        dataEventEmitter.emitter(dataEvent);
        return true;
    }

    /**
     * 添加数据
     * @param swordData
     */
    protected abstract void doPush(T swordData);

    @Override
    public T poll() {
        if(state() != QueueState.START){
            logger.info("队列状态是：{}，不能获取数据", state().name());
            return null;
        }

        T swordData = doPoll();

        //数据添加通知监听器
        DataEvent<T> dataEvent = new DataEvent<>();
        dataEvent.setType(DataEventType.SWORD_DATA_DEL);
        dataEvent.setData(swordData);
        dataEventEmitter.emitter(dataEvent);
        return swordData;
    }

    /**
     * 获取数据
     * @return
     */
    protected abstract T doPoll();


    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public T peek() {
        return queue.peek();
    }


    @Override
    public void registerEventListener(DataEventListener dataEventListener) {
        dataEventEmitter.registerEventListener(dataEventListener);
    }

    @Override
    public void removeEventListener(DataEventListener dataEventListener) {
        dataEventEmitter.removeEventListener(dataEventListener);
    }

    @Override
    public QueueState state() {
        return state;
    }

    public void state(QueueState state) {
        this.state = state;
    }
}
