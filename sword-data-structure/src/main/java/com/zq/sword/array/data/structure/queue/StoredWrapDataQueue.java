package com.zq.sword.array.data.structure.queue;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.data.ObjectDeserializer;
import com.zq.sword.array.data.ObjectSerializer;
import com.zq.sword.array.stream.io.ResourceStore;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
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
 * @description: bitcask类型队列
 * @author: zhouqi1
 * @create: 2018-10-17 19:12
 **/
public class StoredWrapDataQueue<T> extends AbstractQueue<T> implements DataQueue<T> {

    private Logger logger = LoggerFactory.getLogger(StoredWrapDataQueue.class);

    /**
     * 实际存储的队列
     */
    private AbstractDataQueue<T> queue;

    private TaskExecutor taskExecutor;

    private Lock pushLock = new ReentrantLock();
    private Lock pollLock = new ReentrantLock();
    /**
     * 资源存储器
     */
    private ResourceStore resourceStore;

    /**
     * 对象序列化器
     */
    private ObjectSerializer objectSerializer;

    /**
     * 对象反序列化器
     */
    private ObjectDeserializer objectDeserializer;

    private static final int ADD_TYPE = 1;
    private static final int DEL_TYPE = 2;

    public StoredWrapDataQueue(ResourceStore resourceStore, ObjectSerializer objectSerializer, ObjectDeserializer objectDeserializer){
        this(new PrimitiveDataQueue<>(), resourceStore, objectSerializer, objectDeserializer);
    }

    public StoredWrapDataQueue(AbstractDataQueue<T> queue, ResourceStore resourceStore, ObjectSerializer objectSerializer, ObjectDeserializer objectDeserializer){
        logger.info("BitcaskLeftOrderlyQueue init...");
        this.queue = queue;
        state(QueueState.NEW);
        this.resourceStore = resourceStore;
        this.objectSerializer = objectSerializer;
        this.objectDeserializer = objectDeserializer;
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
        logger.info("BitcaskLeftOrderlyQueue end...");
    }

    /*
     *初始化数据
     */
    private void initData(){
        List<T> objectData = readAllObjectData();
        if(objectData != null){
            objectData.forEach(c->queue.doPush(c));
        }
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //每隔一天重新合并数据文件
        taskExecutor.timedExecute(()->{
            logger.info("定时合并文件数据开始---");
            queue.state(QueueState.STOP);

            List<T> objectData = readAllObjectData();
            ObjectResourceStore objectResourceStore = new ObjectResourceStore(this.resourceStore, objectSerializer, objectDeserializer);
            ObjectResourceOutputStream resourceOutputStream = null;
            try {
                resourceOutputStream = objectResourceStore.openOutputStream();
                resourceOutputStream.clearStream();
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
            queue.state(QueueState.START);
            logger.info("定时合并文件数据结束---");
        }, 1, TimeUnit.DAYS);
    }

    /**
     * 从存储中读取所有数据
     * @return
     */
    private List<T> readAllObjectData() {
        List<T> objectData = new LinkedList<>();
        ObjectResourceStore objectResourceStore = new ObjectResourceStore(this.resourceStore, objectSerializer, objectDeserializer);
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


    private void writeResourceStore(T t, int type) {
        ObjectResourceStore objectResourceStore = new ObjectResourceStore(this.resourceStore, objectSerializer, objectDeserializer);
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
    public void registerEventListener(DataEventListener dataEventListener) {
        queue.registerEventListener(dataEventListener);
    }

    @Override
    public void removeEventListener(DataEventListener dataEventListener) {
        queue.registerEventListener(dataEventListener);
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
    public boolean offer(T t) {
        boolean v = false;
        Lock lock = this.pushLock;
        lock.lock();
        try{
            v = queue.offer(t);
            writeResourceStore(t, ADD_TYPE);
        }finally {
            lock.unlock();
        }
        return v;
    }

    @Override
    public T poll() {
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
        return t;
    }

    @Override
    public T peek() {
        return queue.peek();
    }

    @Override
    public QueueState state() {
        return queue.state();
    }

    public void state(QueueState state) {
        queue.state(state);
    }


    /**
     * @program: sword-array
     * @description: bitcask存储
     * @author: zhouqi1
     * @create: 2019-01-14 10:58
     **/
    private static class ObjectResourceStore implements ResourceStore{

        /**
         * 实际的资源存储器
         */
        private ResourceStore resourceStore;

        /**
         * 对象序列化器
         */
        private ObjectSerializer objectSerializer;

        /**
         * 对象反序列化器
         */
        private ObjectDeserializer objectDeserializer;

        public ObjectResourceStore(ResourceStore resourceStore, ObjectSerializer objectSerializer, ObjectDeserializer objectDeserializer) {
            this.resourceStore = resourceStore;
            this.objectSerializer = objectSerializer;
            this.objectDeserializer = objectDeserializer;
        }

        @Override
        public ObjectResourceInputStream openInputStream() throws InputStreamOpenException {
            return new ObjectResourceInputStream(resourceStore.openInputStream(), objectDeserializer);
        }

        @Override
        public ObjectResourceOutputStream openOutputStream() throws OutputStreamOpenException {
            return new ObjectResourceOutputStream(resourceStore.openOutputStream(), objectSerializer);
        }

    }

}
