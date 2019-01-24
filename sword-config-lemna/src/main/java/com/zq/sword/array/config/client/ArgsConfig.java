package com.zq.sword.array.config.client;

/**
 * @program: sword-array
 * @description: 参数配置
 * @author: zhouqi1
 * @create: 2019-01-21 19:06
 **/
public interface ArgsConfig {

    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    String getParam(String key);

    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    String getParam(String key, String defaultValue);

    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    <T> T getParam(String key, Class<T> valueClazz);

    /**
     * 获取指定key的值
     * @param key
     * @return
     */
    <T> T getParam(String key, Class<T> valueClazz, T defaultValue);

    /**
     * 监听指定key的数据变化
     * @param key
     * @return
     */
    void subscribeArgsChanges(String key, ArgsChangeListener listener);
}
