package com.zq.sword.array.common.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 服务上下文
 * @author: zhouqi1
 * @create: 2018-08-01 17:13
 **/
public class ServiceContext {

    private Map<String, Service> services;

    private ServiceContext(){
        services = new ConcurrentHashMap<>();
    }

    public static class ServiceContextSingle{
        public static final ServiceContext SERVICE_CONTEXT = new ServiceContext();
    }

    public static ServiceContext getInstance(){
        return ServiceContextSingle.SERVICE_CONTEXT;
    }

    public <T> void registerService(Class<T> serviceClass, Service service) {
        services.put(serviceClass.getName(), service);
    }

    public <T> T findService(Class<T> serviceClass) {
        return (T)services.get(serviceClass.getName());
    }
}
