package com.zq.sword.array.zpiper.server.piper.config;

import com.zq.sword.array.config.client.ArgsChangeListener;

/**
 * @program: sword-array
 * @description: 动态配置
 * @author: zhouqi1
 * @create: 2019-01-24 10:28
 **/
public interface DynamicConfig extends AppConfig {

    /**
     * 监听指定key的数据变化
     * @param key
     * @return
     */
    void subscribeParamChanges(String key, ArgsChangeListener listener);
}
