package com.zq.sword.array.common.service;

import com.zq.sword.array.common.service.AbstractLifecycle;
import com.zq.sword.array.common.service.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 服务接口
 * @author: zhouqi1
 * @create: 2018-07-23 19:25
 **/
public abstract class AbstractServer extends AbstractLifecycle implements Server {

    private Map<String, Service> services;

    public AbstractServer() {
        services = new ConcurrentHashMap<>();
    }

    @Override
    public void init() {
        super.init();
        if(services != null && !services.isEmpty()){
            for(Service service : services.values()){
                if(service.isInit()){
                    service.destroy();
                }
                service.init();
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if(services != null && !services.isEmpty()){
            for(Service service : services.values()){
                if(service.isStart()){
                    service.stop();
                }
                service.start();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        if(services != null && !services.isEmpty()){
            for(Service service : services.values()){
                if(!service.isStop()){
                    service.stop();
                }
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if(services != null && !services.isEmpty()){
            for(Service service : services.values()){
                if(!service.isDestroy()){
                    service.destroy();
                }
            }
        }
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        if(services != null && !services.isEmpty()){
            for(Service service : services.values()){
                if(service.isStart()){
                    service.stop();
                }
                service.start(serviceConfig);
            }
        }
    }

    @Override
    public <T> void registerService(Class<T> serviceClass, Service service) {
        if(isInit()){
            service.init();
        }
        if(isStart()){
            service.start();
        }
        services.put(serviceClass.getName(), service);

        //服务添加到服务上下文
        ServiceContext.getInstance().registerService(serviceClass, service);
    }

    @Override
    public <T> T findService(Class<T> serviceClass) {
        return (T)services.get(serviceClass.getName());
    }
}
