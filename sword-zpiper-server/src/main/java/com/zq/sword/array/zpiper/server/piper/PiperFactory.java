package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperType;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;

/**
 * @program: sword-array
 * @description: piper工厂
 * @author: zhouqi1
 * @create: 2019-01-24 13:47
 **/
public class PiperFactory {

    /**
     * 创建piper
     * @param piperConfig
     * @return
     */
    public static Piper createPiper(PiperConfig piperConfig){
        Piper piper = null;
        NamePiper namePiper = piperConfig.namePiper();
        PiperType type = namePiper.getType();
        switch (type){
            case SIMPLE:
                piper = new RedisPiper(piperConfig);
                break;
            case PROXY:
                piper = new RedisProxyPiper(piperConfig);
                break;
            default:
                break;

        }
        return piper;
    }
}
