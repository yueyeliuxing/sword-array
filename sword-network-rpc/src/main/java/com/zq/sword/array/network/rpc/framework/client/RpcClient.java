package com.zq.sword.array.network.rpc.framework.client;

import com.zq.sword.array.network.rpc.framework.handler.ProtocolProcessor;

/**
 * @program: sword-array
 * @description: 传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:00
 **/
public interface RpcClient {

    /**
     * 注册业务处理器
     * @param protocolProcessor
     */
    void registerProtocolProcessor(ProtocolProcessor protocolProcessor);

    /**
     * 开启
     */
    void start();


    /**
     * 写入数据
     * @param msg
     */
    void write(Object msg);

    /**
     * 关闭
     */
    void close();

    /**
     * 是否关闭
     * @return
     */
    boolean isClose();
}
