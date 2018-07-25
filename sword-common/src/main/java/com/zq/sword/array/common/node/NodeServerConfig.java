package com.zq.sword.array.common.node;

import java.util.Properties;

/**
 * @program: sword-array
 * @description: NodeServer配置
 * @author: zhouqi1
 * @create: 2018-07-23 17:09
 **/
public class NodeServerConfig {

    private Properties properties;

    public NodeServerConfig(Properties properties) {
        this.properties = properties;
    }

    public String getRedisConnectedAddr(){
        return properties.getProperty(NodeServerConfigKey.REDIS_CONNECT_ADDR);
    }

}
