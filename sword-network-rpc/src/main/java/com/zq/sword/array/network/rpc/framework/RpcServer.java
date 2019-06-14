package com.zq.sword.array.network.rpc.framework;

import com.zq.sword.array.network.rpc.framework.protocol.handler.ProtocolProcessor;

/**
 * @program: sword-array
 * @description: 传输服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:28
 **/
public interface RpcServer {

    /**
     * 开启
     */
    void start();


    /***
     * 服务注册
     * @param service
     */
    void registerService(Object service);

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
