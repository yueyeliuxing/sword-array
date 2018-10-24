package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.SwordCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: redis客户端实现
 * @author: zhouqi1
 * @create: 2018-10-18 20:53
 **/
public class SwordRedisClient implements RedisClient<SwordCommand> {
    private Logger logger = LoggerFactory.getLogger(SwordRedisClient.class);

    private JedisClient jedisClient;

    public SwordRedisClient(RedisConfig redisConfig) {
        jedisClient = new JedisClient();
        jedisClient.initJedis(redisConfig);
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

    @Override
    public void close() {
        jedisClient.close();
    }
}
