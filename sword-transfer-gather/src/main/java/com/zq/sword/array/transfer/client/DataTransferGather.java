package com.zq.sword.array.transfer.client;

/**
 * @program: sword-array
 * @description: 数据传输的收集者
 * @author: zhouqi1
 * @create: 2018-10-24 15:41
 **/
public interface DataTransferGather {

    /**
     * 收集者启动
     */
    void start();

    /**
     * 关闭
     */
    void stop();
}
