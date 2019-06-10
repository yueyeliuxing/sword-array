package com.zq.sword.array.admin.config;

/**
 * @program: sword-array
 * @description: 配置
 * @author: zhouqi1
 * @create: 2019-01-24 10:09
 **/
public interface AppConfig {

    /**
     * 获取参数
     * @param key
     * @return
     */
    String getParam(String key);

    /**
     * 获取参数 没有的话 返回参数值
     * @param key
     * @param defaultValue
     * @return
     */
    String getParam(String key, String defaultValue);

    /**
     * 获取参数值
     * @param key
     * @param valueClazz
     * @param <T>
     * @return
     */
    <T> T getParam(String key, Class<T> valueClazz);
}
