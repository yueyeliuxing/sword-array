package com.zq.sword.array.redis.writer;

import com.zq.sword.array.redis.command.CommandType;
import com.zq.sword.array.redis.command.RedisCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: redis客户端实现
 * @author: zhouqi1
 * @create: 2018-10-18 20:53
 **/
public class SwordRedisClient implements RedisClient<RedisCommand> {
    private Logger logger = LoggerFactory.getLogger(SwordRedisClient.class);

    private JedisClient jedisClient;

    public SwordRedisClient(RedisConfig redisConfig) {
        jedisClient = new JedisClient();
        jedisClient.initJedis(redisConfig);
    }

    @Override
    public boolean write(RedisCommand data) {
        try {
            CommandType type = CommandType.toEnum(data.getType());
            switch (type){
                case SET:
                    jedisClient.saveValueByKey(data.getKey(), data.getValue(), data.getEx());
                    break;
                case INCR:
                    jedisClient.incr(data.getKey());
                    break;
                case DECR:
                    jedisClient.decr(data.getKey());
                    break;
                case SADD:
                    jedisClient.sadd(data.getKey(), data.getMembers());
                    break;
                case HSET:
                    jedisClient.hset(data.getKey(), data.getField(), data.getValue());
                    break;
                case HMSET:
                    jedisClient.hmset(data.getKey(), data.getFields());
                    break;
                case LSET:
                    jedisClient.lset(data.getKey(), data.getIndex(), data.getValue());
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
