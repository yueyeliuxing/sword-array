package com.zq.sword.array.zpiper.server.piper.cluster;

import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperStartState;

/**
 * @program: sword-array
 * @description: master 集群处理
 * @author: zhouqi1
 * @create: 2019-01-21 20:27
 **/
public interface PiperCluster {

    /**
     * piper注册
     * @param piper
     * @return
     */
    boolean register(NamePiper piper, HotspotEventListener<Void> eventListener);

    /**
     * 设置piper 启动 状态
     */
    void setStartState(NamePiper piper, PiperStartState startState);

    /***
     * 关闭
     */
    void close();
}
