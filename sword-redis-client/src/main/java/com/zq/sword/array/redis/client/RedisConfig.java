package com.zq.sword.array.redis.client;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2018-10-18 20:55
 **/
@Data
@ToString
public class RedisConfig {

    private String host;

    private String port;

    private String pass;

    private String timeout;

    private String maxIdle;

    private String maxTotal;

    private String maxWaitMillis;

    private String testOnBorrow;
}
