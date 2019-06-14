package com.zq.sword.array.rpc.api.namer;

import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.List;

/**
 * @program: sword-array
 * @description: piper查询服务
 * @author: zhouqi1
 * @create: 2019-06-14 14:00
 **/
public interface PiperSearchService {

    /**
     * 查询所有piper
     * @return
     */
    List<NamePiper> listPipers();
}
