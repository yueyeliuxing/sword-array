package com.zq.sword.array.redis.handler;


import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.util.JedisClient;
import com.zq.sword.array.redis.util.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据循环处理
 * @author: zhouqi1
 * @create: 2018-10-22 16:27
 **/
public class RedisCycleDisposeHandler implements CycleDisposeHandler<RedisCommand> {

    private Logger logger = LoggerFactory.getLogger(RedisCycleDisposeHandler.class);

    private static final String CYCLE_DISPOSE_KEY = "cycle-dispose-key";

    private static final long SAVE_TIME = 60 * 1000;

    private JedisClient jedisClient;

    public RedisCycleDisposeHandler(RedisConfig redisConfig) {
        jedisClient = new JedisClient();
        jedisClient.initJedis(redisConfig);
    }

    @Override
    public boolean isCycleData(RedisCommand command) {
        byte[] value = jedisClient.hget(CYCLE_DISPOSE_KEY.getBytes(), command.getKey());
        if(value == null){
            return false;
        }
        if(System.currentTimeMillis() - Long.valueOf(new String(value)) < SAVE_TIME){
            return true;
        }
        jedisClient.hdel(CYCLE_DISPOSE_KEY.getBytes(), command.getKey());
        return false;
    }

    @Override
    public boolean addCycleData(RedisCommand command) {
        jedisClient.hset(CYCLE_DISPOSE_KEY.getBytes(), command.getKey(), String.valueOf(System.currentTimeMillis()).getBytes());
        return true;
    }
}
