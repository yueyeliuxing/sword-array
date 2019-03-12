package com.zq.sword.array.zpiper.server.piper.config;

/**
 * @program: sword-array
 * @description: key
 * @author: zhouqi1
 * @create: 2019-01-24 10:40
 **/
public class PiperConfigKey {

    /**
     * piper 绑定监听的端口
     */
    public static final String PIPER_BIND_PORT = "piper.bind.port";

    public static final String PIPER_TYPE= "piper.type";
    public static final String PIPER_DC_NAME= "piper.dc.name";
    public static final String PIPER_UNIT_CATEGORYE= "piper.unit.category";
    public static final String PIPER_UNIT= "piper.unit";
    public static final String PIPER_GROUP= "piper.group";

    /**
     * piper 注册 zk地址
     */
    public static final String PIPER_ZK_CONNECT_ADDRESS = "piper.zk.connect.address";

    public static final String PIPER_REDIS_URI = "piper.redis.uri";

    public static final String PIPER_OTHER_DC_ZK_LOCATIONS = "piper.other.dc.zk.locations";

    /**
     * piper 绑定监听的端口
     */
    public static final String MSG_RESOURCE_LOCATION = "msg.resource.location";
}
