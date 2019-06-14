package com.zq.sword.array.namer.piper;


import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

/**
 * @program: sword-array
 * @description: piper监听器
 * @author: zhouqi1
 * @create: 2019-06-10 13:54
 **/
public interface MetaPiperListener {

    /**
     * 添加
     * @param piper
     */
    void add(NamePiper piper);

    /**
     * 删除
     * @param piper
     */
    void remove(NamePiper piper);
}
