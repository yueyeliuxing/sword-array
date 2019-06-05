package com.zq.sword.array.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 线程执行器
 * @author: zhouqi1
 * @create: 2019-06-05 10:16
 **/
public class ThreadActuator implements Actuator {

    private Logger logger = LoggerFactory.getLogger(AbstractThreadActuator.class);

    private Thread thread;

    public ThreadActuator(Runnable runnable){
        thread = new Thread(runnable);
    }

    public ThreadActuator(String name, Runnable runnable){
        thread = new Thread(runnable);
        thread.setName(name);
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }
}
