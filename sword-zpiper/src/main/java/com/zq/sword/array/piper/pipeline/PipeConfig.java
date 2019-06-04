package com.zq.sword.array.piper.pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 管道参数
 * @author: zhouqi1
 * @create: 2019-06-04 14:43
 **/
public class PipeConfig {

    private Map<String, Object> params;

    public PipeConfig() {
        params = new HashMap<>();
    }

    /**
     * 添加
     * @param key
     * @param value
     */
    public void put(String key, Object value){
        params.put(key, value);
    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public Object get(String key){
        return params.get(key);
    }
}
