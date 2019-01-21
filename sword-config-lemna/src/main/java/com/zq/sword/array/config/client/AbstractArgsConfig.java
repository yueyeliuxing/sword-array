package com.zq.sword.array.config.client;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: 参数配置zk实现
 * @author: zhouqi1
 * @create: 2019-01-21 19:11
 **/
public abstract class AbstractArgsConfig implements ArgsConfig{

    protected ApplicationId id;

    /**
     * 内存参数集
     */
    protected Properties properties;

    /**
     * 监听器
     */
    protected Map<String, List<ArgsChangeListener>> argsChangeListeners;

    public AbstractArgsConfig(ApplicationId id) {
        this.id = id;
        properties = new Properties();
        argsChangeListeners = new ConcurrentHashMap<>();


        //拉取配置
        pullConfig();

        //监听数据变更
        listenArgsConfigChange(new CommonApplicationArgsConfigChangeHandler());
    }

    /**
     * 监听数据变更
     */
    protected abstract void listenArgsConfigChange(ApplicationArgsConfigChangeHandler configChangeHandler);

    /**
     * 拉取配置
     */
    protected abstract void pullConfig();


    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public <T> T get(String key, Class<T> valueClazz) {
        Object value = properties.get(key);
        if(valueClazz.isInstance(value)){
            return valueClazz.cast(value);
        }
        return null;
    }

    @Override
    public void subscribeArgsChanges(String key, ArgsChangeListener listener) {
        List<ArgsChangeListener> listeners = argsChangeListeners.get(key);
        if(listeners == null){
            listeners = new CopyOnWriteArrayList<>();
            argsChangeListeners.put(key, listeners);
        }
        listeners.add(listener);
    }

    /**
     * 参数数据变更处理器
     */
    protected interface ApplicationArgsConfigChangeHandler {

        void handle(String key, Object value);
    }

    /**
     * 通用的处理器
     */
    private class CommonApplicationArgsConfigChangeHandler implements ApplicationArgsConfigChangeHandler {

        @Override
        public void handle(String key, Object value) {
            Object oldValue = properties.get(key);
            if(oldValue == null || !oldValue.equals(value)){
                List<ArgsChangeListener> listeners = argsChangeListeners.get(key);
                if(listeners != null){
                    for(ArgsChangeListener listener : listeners){
                        listener.argsChange(value);
                    }
                }
                properties.put(key, value);
            }
        }
    }
}
