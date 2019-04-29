package com.zq.sword.array.namer;

import com.zq.sword.array.namer.config.PiperConfig;
import com.zq.sword.array.namer.piper.PiperServerController;
import com.zq.sword.array.network.rpc.protocol.NamerServiceProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class DefaultNamer implements Namer {

    private Logger logger = LoggerFactory.getLogger(DefaultNamer.class);

    /**
     * namer服务通信
     */
    private NamerServiceProtocol namerServiceProtocol;

    /**
     * piper监控器
     */
    private PiperServerController piperNameController;


    public DefaultNamer(PiperConfig config) {

        //创建namer通信服务
        namerServiceProtocol =  new NamerServiceProtocol(config.namerLocation());

        //创建piper控制器
        piperNameController = new PiperServerController();

        //设置piper事件处理器
        namerServiceProtocol.setNamerServiceProcessor(piperNameController);


    }

    @Override
    public void start() {

        namerServiceProtocol.start();
    }

    @Override
    public void stop() {
        namerServiceProtocol.stop();
    }
}
