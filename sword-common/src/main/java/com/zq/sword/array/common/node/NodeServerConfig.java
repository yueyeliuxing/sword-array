package com.zq.sword.array.common.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class NodeServerConfig {

    private Logger logger = LoggerFactory.getLogger(NodeServerConfig.class);

    private Properties properties;

    public NodeServerConfig(String propertiesString) {
        try{
            properties = new Properties();
            properties.load(new ByteArrayInputStream(propertiesString.getBytes(Charset.defaultCharset())));
        }catch (Exception e){
            logger.error("加载服务配置文件转换为Properties出错", e);
        }
    }

    public NodeServerConfig(Properties properties) {
        this.properties = properties;
    }

    public String getRedisConnectedAddr(){
        return properties.getProperty(NodeServerConfigKey.REDIS_CONNECT_ADDR);
    }

}
