package com.zq.sword.array.admin.config;

/**
 * @program: sword-array
 * @description: 参数配置
 * @author: zhouqi1
 * @create: 2019-01-24 10:21
 **/
public interface PropertiesConfig extends AppConfig{

    /**
     * 设置参数
     * @param key
     * @param value
     */
    void setParam(String key, String value);
}
