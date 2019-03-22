package com.zq.sword.array.network.rpc.client;

import com.zq.sword.array.network.rpc.handler.TransferHandler;

/**
 * @program: sword-array
 * @description: 传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:00
 **/
public interface RpcClient {

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

    /**
     * 是否关闭
     * @return
     */
    boolean isClose();
}
