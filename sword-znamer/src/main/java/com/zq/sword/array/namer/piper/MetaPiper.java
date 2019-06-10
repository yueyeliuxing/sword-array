package com.zq.sword.array.namer.piper;

import com.zq.sword.array.network.rpc.protocol.dto.piper.NamePiper;

/**
 * @program: sword-array
 * @description: 元Piper
 * @author: zhouqi1
 * @create: 2019-06-10 09:43
 */
public class MetaPiper {

    /**
     * 地址 ip:port
     */
    private String location;

    /**
     * 状态
     */
    private PiperState state;

    public MetaPiper(String location) {
        this.location = location;
        state = PiperState.STARTED;
    }

    public String location(){
        return location;
    }

    public PiperState state(){
        return state;
    }
}
