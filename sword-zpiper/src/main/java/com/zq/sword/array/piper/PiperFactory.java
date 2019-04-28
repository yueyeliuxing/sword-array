package com.zq.sword.array.piper;

import com.zq.sword.array.piper.config.PiperConfig;

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
        return new RedisPiper(piperConfig);
    }
}
