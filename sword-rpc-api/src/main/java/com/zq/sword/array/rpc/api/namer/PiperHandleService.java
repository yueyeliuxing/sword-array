package com.zq.sword.array.rpc.api.namer;

import com.zq.sword.array.rpc.api.namer.dto.NamePiper;
import com.zq.sword.array.rpc.api.namer.dto.JobCommand;

/**
 * @program: sword-array
 * @description: piper处理
 * @author: zhouqi1
 * @create: 2019-06-14 14:01
 **/
public interface PiperHandleService {

    /**
     * 处理piper注册
     * @param namePiper
     */
    void registerPiper(NamePiper namePiper);

    /**
     * 处理JobCommand 请求
     * @param namePiper
     */
    JobCommand requestJobCommand(NamePiper namePiper);
}
