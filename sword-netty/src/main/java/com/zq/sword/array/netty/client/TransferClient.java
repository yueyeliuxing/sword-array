package com.zq.sword.array.netty.client;

import com.zq.sword.array.netty.handler.TransferHandler;

/**
 * @program: sword-array
 * @description: 传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:00
 **/
public interface TransferClient {

    /**
     * 注册业务处理器
     * @param transferHandler
     */
    void registerTransferHandler(TransferHandler transferHandler);

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 重启
     */
    void restart();
}
