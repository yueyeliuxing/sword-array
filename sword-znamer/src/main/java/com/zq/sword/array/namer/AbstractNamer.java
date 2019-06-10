package com.zq.sword.array.namer;

import com.zq.sword.array.network.rpc.protocol.NamerServiceProtocol;
import com.zq.sword.array.network.rpc.protocol.processor.NamerServiceProcessor;

/**
 * @program: sword-array
 * @description: 抽象namer
 * @author: zhouqi1
 * @create: 2019-06-10 10:25
 **/
public abstract class AbstractNamer implements Namer {

    /**
     * namer服务通信
     */
    private NamerServiceProtocol namerServiceProtocol;

    public AbstractNamer(String location) {
        //创建namer通信服务
        namerServiceProtocol =  new NamerServiceProtocol(location);
    }

    /**
     * 设置处理器
     * @param namerServiceProcessor
     */
    public void setNamerServiceProcessor(NamerServiceProcessor namerServiceProcessor) {
        namerServiceProtocol.setNamerServiceProcessor(namerServiceProcessor);
    }

    @Override
    public void start() {

        namerServiceProtocol.start();
    }

    @Override
    public void shutdown() {
        namerServiceProtocol.stop();
    }
}
