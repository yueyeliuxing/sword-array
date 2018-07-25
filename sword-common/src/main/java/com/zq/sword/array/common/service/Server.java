package com.zq.sword.array.common.service;

import com.zq.sword.array.common.service.Lifecycle;

/**
 * @program: sword-array
 * @description: 服务容器
 * @author: zhouqi1
 * @create: 2018-07-25 10:44
 **/
public interface Server extends Lifecycle {

    /**
     * 开启服务
     */
    void start(ServiceConfig serviceConfig);

    /**
     * 注册服务
     * @param service
     */
    <T> void registerService(Class<T> serviceClass, Service service);

    /**
     * 获取服务
     * @param serviceClass
     * @param <T>
     * @return
     */
    <T> T findService(Class<T> serviceClass);
}
