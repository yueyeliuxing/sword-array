package com.zq.sword.array.namer;

import com.zq.sword.array.namer.config.PiperConfig;

/**
 * @program: sword-array
 * @description: piper工厂
 * @author: zhouqi1
 * @create: 2019-01-24 13:47
 **/
public class NamerFactory {

    /**
     * 创建piper
     * @param piperConfig
     * @return
     */
    public static Namer createPiper(PiperConfig piperConfig){
        return new DefaultNamer(piperConfig);
    }
}
