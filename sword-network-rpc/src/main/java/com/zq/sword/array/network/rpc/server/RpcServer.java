package com.zq.sword.array.network.rpc.server;

/**
 * @program: sword-array
 * @description: 传输服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:28
 **/
public interface RpcServer {

    /**
     *  地址
     * @return
     */
    String host();

    /**
     * 端口
     * @return
     */
    int port();

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 是否启动成功
     * @return
     */
    boolean started();
}
