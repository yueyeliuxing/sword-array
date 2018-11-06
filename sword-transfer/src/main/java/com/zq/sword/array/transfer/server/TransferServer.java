package com.zq.sword.array.transfer.server;

import com.zq.sword.array.transfer.handler.TransferHandler;

/**
 * @program: sword-array
 * @description: 传输服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:28
 **/
public interface TransferServer {


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
