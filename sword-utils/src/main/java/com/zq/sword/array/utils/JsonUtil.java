package com.zq.sword.array.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @program: sword-array
 * @description: JSON 处理
 * @author: zhouqi1
 * @create: 2018-10-17 18:40
 **/
public class JsonUtil {

    /**
     * 解析json字符串
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parse(String json, Class<T> clazz){
        return JSON.parseObject(json, clazz);
    }

    /**
     * 对象生成json字符串
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj){
        return JSONObject.toJSONString(obj);
    }
}
