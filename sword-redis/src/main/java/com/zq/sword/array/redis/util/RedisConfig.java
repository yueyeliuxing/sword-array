package com.zq.sword.array.redis.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2018-10-18 20:55
 **/
@Data
@ToString
@NoArgsConstructor
public class RedisConfig {

    private String host;

    private String port;

    private String pass;

    private String timeout;

    private String maxIdle;

    private String maxTotal;

    private String maxWaitMillis;

    private String testOnBorrow;

    public RedisConfig(String redisUri) {
        URI uri = null;
        try {
            uri = new URI(redisUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.host = uri.getHost();
        this.port = uri.getPort()+"";
        this.pass = null;
        this.timeout = "5000";
        this.maxIdle = "500";
        this.maxTotal = "0";
        this.maxWaitMillis = "0";
        this.testOnBorrow = "true";
    }

    public RedisConfig(String host, String port, String pass) {
        this.host = host;
        this.port = port;
        this.pass = pass;
        this.timeout = "5000";
        this.maxIdle = "500";
        this.maxTotal = "0";
        this.maxWaitMillis = "0";
        this.testOnBorrow = "true";
    }

    public RedisConfig(String host, String port, String pass, String timeout, String maxIdle, String maxTotal, String maxWaitMillis, String testOnBorrow) {
        this.host = host;
        this.port = port;
        this.pass = pass;
        this.timeout = timeout;
        this.maxIdle = maxIdle;
        this.maxTotal = maxTotal;
        this.maxWaitMillis = maxWaitMillis;
        this.testOnBorrow = testOnBorrow;
    }
}
