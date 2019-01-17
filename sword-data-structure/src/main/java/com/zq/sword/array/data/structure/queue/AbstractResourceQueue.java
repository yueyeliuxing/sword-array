package com.zq.sword.array.data.structure.queue;

import com.zq.sword.array.common.event.DefaultHotspotEventEmitter;
import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.common.event.HotspotEventType;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectResource;
import com.zq.sword.array.stream.io.object.ObjectResourceInputStream;
import com.zq.sword.array.stream.io.object.ObjectResourceOutputStream;
import com.zq.sword.array.tasks.SingleTaskExecutor;
import com.zq.sword.array.tasks.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: sword-array
 * @description: 对列抽象
 * @author: zhouqi1
 * @create: 2018-12-25 14:58
 **/
public abstract class AbstractResourceQueue<T> extends AbstractQueue<T> implements ResourceQueue<T> {

    private Logger logger = LoggerFactory.getLogger(AbstractResourceQueue.class);

    /***
     * 队列的状态
     */
    protected volatile QueueState state;

    /**
     * 内存队列
     */
    protected Queue<T> queue;

    private TaskExecutor taskExecutor;

    private Lock pushLock = new ReentrantLock();
    private Lock pollLock = new ReentrantLock();
    /**
     * 资源存储器
     */
    private ObjectResource resource;

    private static final int ADD_TYPE = 1;
    private static final int DEL_TYPE = 2;

    /**
     * 事件发射器
     */
    protected DefaultHotspotEventEmitter dataEventEmitter;

    public AbstractResourceQueue(Queue<T> queue, ObjectResource objectResource) {
        this.queue = queue;
        dataEventEmitter = new DefaultHotspotEventEmitter();
        logger.info("AbstractResourceQueue init...");
        this.queue = queue;
        state(QueueState.NEW);
        this.resource = objectResource;
        this.taskExecutor = new SingleTaskExecutor();

        /**
         * 初始化数据
         */
        initData();

        /**
         * 初始化任务
         */
        startTasks();


        state(QueueState.START);
        logger.info("AbstractResourceQueue end...");
    }

    /*
     *初始化数据
     */
    private void initData(){
        List<T> objectData = readAllObjectData();
        if(objectData != null){
            objectData.forEach(c->queue.offer(c));
        }
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //每隔一天重新合并数据文件
        taskExecutor.timedExecute(()->{
            logger.info("定时合并文件数据开始---");
            state(QueueState.STOP);

            List<T> objectData = readAllObjectData();
            ObjectResource objectResource = this.resource;
            objectResource.close();
            ObjectResourceOutputStream resourceOutputStream = null;
            try {
                resourceOutputStream = objectResource.openOutputStream();
                if(objectData != null && !objectData.isEmpty()){
                    for(T t : objectData){
                        resourceOutputStream.writeInt(ADD_TYPE);
                        resourceOutputStream.writeObject(t);
                    }
                }
            } catch (OutputStreamOpenException | IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    resourceOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            state(QueueState.START);
            logger.info("定时合并文件数据结束---");
        }, 1, TimeUnit.DAYS);
    }

    /**
     * 从存储中读取所有数据
     * @return
     */
    private List<T> readAllObjectData() {
        List<T> objectData = new LinkedList<>();
        ObjectResource objectResourceStore = this.resource;
        ObjectResourceInputStream resourceInputStream = null;
        try {
            resourceInputStream = objectResourceStore.openInputStream();
            if(resourceInputStream.available() <= 0){
                return objectData;
            }
            T t = null;
            int type = 0;
            do {
                try{
                    type = resourceInputStream.readInt();
                    t = (T)resourceInputStream.readObject();
                    if(t == null){
                        break;
                    }
                    if(type == ADD_TYPE){
                        objectData.add(t);
                    }else if(type == DEL_TYPE){
                        if(objectData.isEmpty()){
                            throw new RuntimeException("数据读取失败");
                        }
                        objectData.remove(t);
                    }
                }catch (EOFException e){
                    type = 0;
                }
            }while (type != 0);

        } catch (InputStreamOpenException | IOException e) {
            logger.error("队列从存储中读数据错误", e);
        }finally {
            try {
                resourceInputStream.close();
            } catch (IOException e) {
                logger.error("存储输入流关闭错误", e);
            }
        }
        return objectData;
    }

    /**
     * 把数据写入输出流中
     * @param t 数据
     * @param type 操作类型
     */
    private void writeResourceStore(T t, int type) {
        ObjectResource objectResourceStore = this.resource;
        ObjectResourceOutputStream resourceOutputStream = null;
        try {
            resourceOutputStream = objectResourceStore.openOutputStream();
            resourceOutputStream.writeInt(type);
            resourceOutputStream.writeObject(t);
        } catch (OutputStreamOpenException | IOException e) {
            logger.error("写入存储输出流中出错", e);
        }finally {
            try {
                resourceOutputStream.close();
            } catch (IOException e) {
                logger.error("存储输出流关闭出错", e);
            }
        }
    }

    @Override
    public boolean offer(T t) {
        if(state() != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }

        boolean v = false;
        Lock lock = this.pushLock;
        lock.lock();
        try{
            v = queue.offer(t);
            writeResourceStore(t, ADD_TYPE);
        }finally {
            lock.unlock();
        }

        //数据添加通知监听器
        HotspotEvent<T> dataEvent = new HotspotEvent<>();
        dataEvent.setType(HotspotEventType.SWORD_DATA_ADD);
        dataEvent.setData(t);
        dataEventEmitter.emitter(dataEvent);
        return v;
    }

    @Override
    public T poll() {
        if(state() != QueueState.START){
            logger.info("队列状态是：{}，不能获取数据", state().name());
            return null;
        }

        T t = null;
        Lock lock = this.pollLock;
        lock.lock();
        try{
            t = queue.poll();
            if(t != null){
                writeResourceStore(t, DEL_TYPE);
            }
        }finally {
            lock.unlock();
        }

        //数据添加通知监听器
        HotspotEvent<T> dataEvent = new HotspotEvent<>();
        dataEvent.setType(HotspotEventType.SWORD_DATA_DEL);
        dataEvent.setData(t);
        dataEventEmitter.emitter(dataEvent);
        return t;
    }

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
