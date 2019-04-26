package com.zq.sword.array.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 抽象的线程执行器
 * @author: zhouqi1
 * @create: 2019-01-18 14:00
 **/
public abstract class AbstractThreadActuator implements Actuator {

    private Logger logger = LoggerFactory.getLogger(AbstractThreadActuator.class);

    private Thread thread;

    protected volatile boolean isClose = false;

    @Override
    public void start() {
        thread = new Thread(()->{
            run();
        });
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                handleEx(e);
            }
        });
        thread.start();
    }

    /**
     * 处理异常
     * @param e
     */
    public void handleEx(Throwable e){

    }

    public abstract void run();

    @Override
    public void stop() {
        isClose = true;
    }
}
