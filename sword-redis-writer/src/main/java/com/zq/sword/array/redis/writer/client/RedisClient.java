package com.zq.sword.array.redis.writer.client;

import com.zq.sword.array.redis.command.CommandType;
import com.zq.sword.array.redis.command.RedisCommand;
import com.zq.sword.array.redis.writer.data.RedisConfig;

/**
 * @program: sword-array
 * @description: redis 客户端
 * @author: zhouqi1
 * @create: 2019-01-24 15:44
 **/
public class RedisClient {

    private JedisClient jedisClient;

    public RedisClient(RedisConfig redisConfig) {
        this.jedisClient = new JedisClient();
        this.jedisClient.initJedis(redisConfig);
    }

    public void write(RedisCommand command) throws Exception {
        CommandType type = CommandType.toEnum(command.getType());
        switch (type){
            case SET:
                jedisClient.saveValueByKey(command.getKey(), command.getValue(), command.getEx());
                break;
            case INCR:
                jedisClient.incr(command.getKey());
                break;
            case DECR:
                jedisClient.decr(command.getKey());
                break;
            case SADD:
                jedisClient.sadd(command.getKey(), command.getMembers());
                break;
            case HSET:
                jedisClient.hset(command.getKey(), command.getField(), command.getValue());
                break;
            case HMSET:
                jedisClient.hmset(command.getKey(), command.getFields());
                break;
            case LSET:
                jedisClient.lset(command.getKey(), command.getIndex(), command.getValue());
                break;
            default:
                break;
        }
    }

    public void close(){
        jedisClient.close();
    }
}
