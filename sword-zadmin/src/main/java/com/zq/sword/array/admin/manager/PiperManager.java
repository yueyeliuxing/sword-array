package com.zq.sword.array.admin.manager;


import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.List;

/**
 * @program: sword-array
 * @description: piper处理器
 * @author: zhouqi1
 * @create: 2019-06-11 15:42
 **/
public interface PiperManager {

    /**
     *  获取所有piper信息
     * @return
     */
    List<NamePiper> allPiper();
}
