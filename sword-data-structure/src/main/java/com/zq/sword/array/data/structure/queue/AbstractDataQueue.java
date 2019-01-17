package com.zq.sword.array.data.structure.queue;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.common.event.DefaultHotspotEventEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractQueue;
import java.util.Iterator;
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
    protected DefaultHotspotEventEmitter dataEventEmitter;

    public AbstractDataQueue(Queue<T> queue) {
        this.queue = queue;
        dataEventEmitter = new DefaultHotspotEventEmitter();
    }

    @Override
    public boolean offer(T t) {
        if(state() != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }

        doPush(t);

        //数据添加通知监听器
        HotspotEvent<T> dataEvent = new HotspotEvent<>();
        dataEvent.setType(HotspotEventType.SWORD_DATA_ADD);
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
        HotspotEvent<T> dataEvent = new HotspotEvent<>();
        dataEvent.setType(HotspotEventType.SWORD_DATA_DEL);
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
    public void registerEventListener(HotspotEventListener dataEventListener) {
        dataEventEmitter.registerEventListener(dataEventListener);
    }

    @Override
    public void removeEventListener(HotspotEventListener dataEventListener) {
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
