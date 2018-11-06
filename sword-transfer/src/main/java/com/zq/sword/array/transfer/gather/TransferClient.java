package com.zq.sword.array.transfer.gather;

import com.zq.sword.array.transfer.handler.TransferHandler;

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
    void connect();

    /**
     * 关闭
     */
    void disconnect();

    /**
     * 重启
     */
    void reconnect();
}
