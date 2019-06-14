package com.zq.sword.array.piper;

import com.zq.sword.array.network.rpc.framework.NettyRpcClient;
import com.zq.sword.array.network.rpc.framework.NettyRpcServer;
import com.zq.sword.array.network.rpc.framework.RpcClient;
import com.zq.sword.array.network.rpc.framework.RpcServer;
import com.zq.sword.array.piper.config.PiperConfig;

/**
 * @program: sword-array
 * @description: 抽象piper
 * @author: zhouqi1
 * @create: 2019-06-04 19:47
 **/
public abstract class AbstractPiper implements Piper {

    /**
     * Piper服务提供通信
     */
    private RpcServer rpcServer;

    /**
     * name客户端
     */
    private RpcClient namerRpcClient;

    public AbstractPiper(PiperConfig config) {
        String piperLocation = config.piperLocation();
        String[] params = piperLocation.split(":");
        this.rpcServer = new NettyRpcServer(Integer.parseInt(params[1]));

        String namerLocation = config.namerLocation();
        String[] ps = namerLocation.split(":");
        namerRpcClient = new NettyRpcClient(ps[0], Integer.parseInt(ps[1]));
    }

    /**
     * 注册服务
     * @param service
     */
    public void registerService(Object service){
        rpcServer.registerService(service);
    }

    /**
     * 获取服务
     * @param serviceType
     * @param <T>
     * @return
     */
    public <T>  T getService(Class<T> serviceType){
        return (T)namerRpcClient.getProxy(serviceType);
    }

    @Override
    public void start() {
        rpcServer.start();
        namerRpcClient.start();
    }

    @Override
    public void shutdown() {
        rpcServer.shutdown();
        namerRpcClient.close();
    }
}
