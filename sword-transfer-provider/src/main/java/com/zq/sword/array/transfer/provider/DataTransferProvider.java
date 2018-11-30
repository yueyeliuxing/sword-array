package com.zq.sword.array.transfer.provider;

/**
 * @program: sword-array
 * @description: 数据传输提供者
 * @author: zhouqi1
 * @create: 2018-10-24 16:53
 **/
public interface DataTransferProvider {

    /**
     * 提供者启动
     */
    void start();

    /**
     * 提供者关闭
     */
    void stop();

    /**
     * 是否启动成功
     * @return
     */
    boolean started();
}
