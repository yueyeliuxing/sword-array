package com.zq.sword.array.common.service;

import com.zq.sword.array.common.node.NodeServerId;

/**
 * @program: sword-array
 * @description: 服务配置
 * @author: zhouqi1
 * @create: 2018-07-23 21:04
 **/
public class ServiceConfig {

    /**
     * 获取ID
     * @return
     */
    public NodeServerId getId(){
        return null;
    }

    /**
     * 获取指定key对应的value
     * @param key
     * @return value
     */
    public String getProperty(String key){
        return null;
    }

    /**
     * 获取指定key对应的value
     * @param key
     * @return value
     */
    public <T> T getProperty(String key, Class<T> type){
        return null;
    }
}
