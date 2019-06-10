package com.zq.sword.array.piper;

import com.zq.sword.array.network.rpc.protocol.PiperServiceProtocol;
import com.zq.sword.array.network.rpc.protocol.processor.PiperServiceProcessor;

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
    private PiperServiceProtocol piperServiceProtocol;

    public AbstractPiper(String piperLocation) {
        /**
         * Piper服务 接收其他piper或者namer的数据
         */
        piperServiceProtocol = new PiperServiceProtocol(piperLocation);
    }

    /**
     * 注册服务处理器
     * @param piperServiceProcessor
     */
    public void registerServiceProcessor(PiperServiceProcessor piperServiceProcessor){
        piperServiceProtocol.setPiperServiceProcessor(piperServiceProcessor);
    }

    @Override
    public void start() {
        piperServiceProtocol.start();
    }

    @Override
    public void shutdown() {
        piperServiceProtocol.stop();
    }
}
