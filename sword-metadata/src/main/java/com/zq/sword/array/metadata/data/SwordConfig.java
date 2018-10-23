package com.zq.sword.array.metadata.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @program: sword-array
 * @description: Sword配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class SwordConfig {

    private Logger logger = LoggerFactory.getLogger(SwordConfig.class);

    private Properties properties;

    public SwordConfig(String propertiesString) {
        try{
            properties = new Properties();
            properties.load(new ByteArrayInputStream(propertiesString.getBytes(Charset.defaultCharset())));
        }catch (Exception e){
            logger.error("加载服务配置文件转换为Properties出错", e);
        }
    }

    public SwordConfig(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue){
        return properties.getProperty(key, defaultValue);
    }

    public <T> T getProperty(String key, Class<T> valueClass){
        Object value =  properties.get(key);
        if(valueClass.isInstance(value)){
            return valueClass.cast(value);
        }
        return null;
    }

}
