package com.zq.sword.array.zpiper.server.piper.job;

import java.util.List;

/**
 * @program: sword-array
 * @description: Piper连接变化的监听器
 * @author: zhouqi1
 * @create: 2019-04-26 14:43
 **/
public interface PiperChangeListener {

    /**
     * 新增的piper连接
     * @param piperLocations
     */
     void increment(List<String> piperLocations);

    /**
     * 减少的piper连接
     * @param piperLocations
     */
     void decrease(List<String> piperLocations);
}
