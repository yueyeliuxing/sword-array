package com.zq.sword.array.network.rpc.framework.server;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;

/**
 * @program: sword-array
 * @description: 传输服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:28
 **/
public interface RpcServer {

    /**
     * 注册协议处理器
     * @param protocolProcessor
     */
    void registerProtocolProcessor(ProtocolProcessor protocolProcessor);

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void close();

    /**
     * 是否启动成功
     * @return
     */
    boolean started();
}
