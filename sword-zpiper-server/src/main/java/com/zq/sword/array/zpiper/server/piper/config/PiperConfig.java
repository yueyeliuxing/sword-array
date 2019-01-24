package com.zq.sword.array.zpiper.server.piper.config;

import com.zq.sword.array.config.client.ApplicationId;
import com.zq.sword.array.config.client.ArgsChangeListener;
import com.zq.sword.array.config.client.ArgsConfig;
import com.zq.sword.array.config.client.ZkArgsConfig;
import com.zq.sword.array.redis.writer.RedisConfig;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Properties;

/**
 * @program: sword-array
 * @description: piper配置参数
 * @author: zhouqi1
 * @create: 2019-01-23 16:03
 **/
public class PiperConfig implements PropertiesConfig, DynamicConfig{

    private Environment environment;

    private Properties properties;

    private ArgsConfig argsConfig;

    public PiperConfig(Environment environment) {
        this.environment = environment;
        this.argsConfig = new ZkArgsConfig(environment.getProperty(PiperConfigKey.PIPER_ZK_CONNECT_ADDRESS), new ApplicationId());
        this.properties = new Properties();
    }

    public int bindPort() {
        return getParam(PiperConfigKey.PIPER_BIND_PORT, Integer.class);
    }

    public String msgResourceLocation() {
        return null;
    }

    public String zkConnection() {
        return  null;
    }

    /**
     * piperID 生成
     * @return
     */
    public long piperId() {
        return 0;
    }

    public String zkLocation() {
        return null;
    }

    public String piperLocation() {
        return null;
    }

    @Override
    public void setParam(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    public String getParam(String key) {
        String value = argsConfig.getParam(key);
        if(value == null){
            value = properties.getProperty(key);
            if(value == null){
                value = environment.getProperty(key);
            }
        }
        return value;
    }

    @Override
    public String getParam(String key, String defaultValue) {
        String value = argsConfig.getParam(key, defaultValue);
        if(value == null){
            value = properties.getProperty(key, defaultValue);
            if(value == null){
                value = environment.getProperty(key, defaultValue);
            }
        }
        return value;
    }

    @Override
    public <T> T getParam(String key, Class<T> valueClazz) {
        T value = argsConfig.getParam(key, valueClazz);
        if(value == null){
            Object v = properties.get(key);
            if(v != null && valueClazz.isInstance(v)){
                value = valueClazz.cast(v);
            }
            if(value == null){
                value = environment.getProperty(key, valueClazz);
            }
        }
        return value;
    }

    @Override
    public void subscribeParamChanges(String key, ArgsChangeListener listener) {
        argsConfig.subscribeArgsChanges(key, listener);
    }

    public NamePiper namePiper() {
        return null;
    }

    public String redisUri() {
        return null;
    }

    public String redisWriteTempFilePath() {
        return null;
    }

    public RedisConfig redisConfig() {
        return null;
    }

    public List<String> otherDcZkLocations() {
        return null;
    }
}
