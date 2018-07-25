package com.zq.sword.array.common.service;

/**
 * @program: sword-array
 * @description: 服务接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:25
 **/
public interface Service extends Lifecycle {

    /**
     * 开启
     */
    void start(ServiceConfig serviceConfig);
}
