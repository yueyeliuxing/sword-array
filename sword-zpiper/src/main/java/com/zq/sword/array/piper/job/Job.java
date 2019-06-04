package com.zq.sword.array.piper.job;

/**
 * @program: sword-array
 * @description: 任务
 * @author: zhouqi1
 * @create: 2019-04-24 16:31
 **/
public interface Job {

    /**
     * 任务名称
     * @return
     */
    String name();


    /**
     * 任务状态
     * @return
     */
    int state();

    /**
     * 启动
     */
    void start();

    /**
     * 重启
     */
    void restart();

    /**
     * 销毁
     */
    void destroy();

}
