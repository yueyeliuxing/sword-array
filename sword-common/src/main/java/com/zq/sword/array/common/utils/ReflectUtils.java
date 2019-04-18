package com.zq.sword.array.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.misc.ReflectUtil;

import java.lang.reflect.Field;

/**
 * @program: sword-array
 * @description: 反射工具类
 * @author: zhouqi1
 * @create: 2019-04-18 11:25
 **/
public class ReflectUtils {

    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 得到指定字段的值
     * @param result
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object result, String fieldName){
        try {
            Field field = result.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(result);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建数据实体
     * @param clazz
     * @return
     */
    public static  <T> T newInstance(Class<T> clazz){
        try {
            return (T) ReflectUtil.newInstance(clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("反射失败", e);
            return null;
        }
    }
}
