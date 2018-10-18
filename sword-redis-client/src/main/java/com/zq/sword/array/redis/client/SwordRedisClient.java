package com.zq.sword.array.redis.client;

import com.zq.sword.array.common.data.SwordCommand;
import com.zq.sword.array.redis.client.service.impl.DefaultRedisWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @program: sword-array
 * @description: redis客户端实现
 * @author: zhouqi1
 * @create: 2018-10-18 20:53
 **/
public class SwordRedisClient implements RedisClient<SwordCommand> {
    private Logger logger = LoggerFactory.getLogger(DefaultRedisWriterService.class);

    private JedisClient jedisClient;

    public SwordRedisClient(RedisConfig redisConfig) {
        jedisClient = new JedisClient();
        jedisClient.initJedis(getProperties(redisConfig));
    }

    private Properties getProperties(RedisConfig redisConfig){
        Properties properties = new Properties();
        properties.setProperty("redis.host", redisConfig.getHost());
        properties.setProperty("redis.port", redisConfig.getPort());
        properties.setProperty("redis.pass", redisConfig.getPass());
        properties.setProperty("redis.timeout", redisConfig.getTimeout());
        properties.setProperty("redis.maxIdle", redisConfig.getMaxIdle());
        properties.setProperty("redis.maxTotal", redisConfig.getMaxTotal());
        properties.setProperty("redis.maxWaitMillis", redisConfig.getMaxWaitMillis());
        properties.setProperty("redis.testOnBorrow", redisConfig.getTestOnBorrow());
        return properties;
    }

    @Override
    public boolean write(SwordCommand data) {
        try {
            switch (data.getType()){
                case (byte)1:
                    jedisClient.saveValueByKey(0, data.getKey().getBytes(), data.getValue().getBytes(), data.getEx());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("write data:{} error：{}", data, e);
            return false;
        }
        return true;
    }
}
