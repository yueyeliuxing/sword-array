package com.zq.sword.array.common.service;

/**
 * @program: sword-array
 * @description: 生命周期接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:27
 **/
public interface Lifecycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void stop();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 是否初始化
     * @return
     */
    boolean isInit();

    /**
     * 是否开启
     * @return
     */
    boolean isStart();

    /**
     * 是否关闭
     * @return
     */
    boolean isStop();

    /**
     * 是否销毁
     * @return
     */
    boolean isDestroy();
}
