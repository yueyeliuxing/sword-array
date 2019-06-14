package com.zq.sword.array.namer;

import com.zq.sword.array.network.rpc.framework.NettyRpcServer;
import com.zq.sword.array.network.rpc.framework.RpcServer;

/**
 * @program: sword-array
 * @description: 抽象namer
 * @author: zhouqi1
 * @create: 2019-06-10 10:25
 **/
public abstract class AbstractNamer implements Namer {

    /**
     * Piper服务提供通信
     */
    private RpcServer rpcServer;

    public AbstractNamer(String location) {
        //创建namer通信服务
        String[] params = location.split(":");
        this.rpcServer = new NettyRpcServer(Integer.parseInt(params[1]));
    }

    /**
     * 注册服务
     * @param service
     */
    public void registerService(Object service){
        rpcServer.registerService(service);
    }

    @Override
    public void start() {
        rpcServer.start();
    }

    @Override
    public void shutdown() {
        rpcServer.shutdown();
    }
}
