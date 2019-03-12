package com.zq.sword.array.zpiper.server.piper.config;

import com.google.common.collect.Lists;
import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.config.client.ApplicationId;
import com.zq.sword.array.config.client.ArgsChangeListener;
import com.zq.sword.array.config.client.ArgsConfig;
import com.zq.sword.array.config.client.ZkArgsConfig;
import com.zq.sword.array.redis.util.RedisConfig;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperType;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;
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
        this.argsConfig = new ZkArgsConfig(environment.getProperty(PiperConfigKey.PIPER_ZK_CONNECT_ADDRESS),
                new ApplicationId());
        this.properties = new Properties();
    }

    public int bindPort() {
        return getParam(PiperConfigKey.PIPER_BIND_PORT, Integer.class);
    }

    public String msgResourceLocation() {
        return getParam(PiperConfigKey.MSG_RESOURCE_LOCATION);
    }

    /**
     * piperID 生成
     * @return
     */
    public long piperId() {
        return Objects.hashCode(piperLocation());
    }

    public String zkLocation() {
        return getParam(PiperConfigKey.PIPER_ZK_CONNECT_ADDRESS);
    }

    public String piperLocation() {
        return IPUtil.getServerIp()+":"+bindPort();
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
        return new NamePiper(piperId(),
                PiperType.valueOf(getParam(PiperConfigKey.PIPER_TYPE)),
                getParam(PiperConfigKey.PIPER_DC_NAME),
                getParam(PiperConfigKey.PIPER_UNIT_CATEGORYE),
                getParam(PiperConfigKey.PIPER_UNIT),
                getParam(PiperConfigKey.PIPER_GROUP),
                piperLocation());
    }

    public String redisUri() {
        return getParam(PiperConfigKey.PIPER_REDIS_URI);
    }

    public RedisConfig redisConfig() {
        return new RedisConfig(redisUri());
    }

    public List<String> otherDcZkLocations() {
        String otherDcZkLocations = getParam(PiperConfigKey.PIPER_OTHER_DC_ZK_LOCATIONS);
        return Lists.newArrayList(otherDcZkLocations.split(","));
    }
}
