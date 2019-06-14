package com.zq.sword.array.network.rpc.framework;

import com.zq.sword.array.network.rpc.framework.protocol.handler.ProtocolProcessor;

/**
 * @program: sword-array
 * @description: 传输客户端
 * @author: zhouqi1
 * @create: 2018-08-01 20:00
 **/
public interface RpcClient {

    /**
     * 开启
     */
    void start();

    /**
     * 得到代理
     * @param serviceInterface
     * @return
     */
    Object getProxy(Class<?> serviceInterface);


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
