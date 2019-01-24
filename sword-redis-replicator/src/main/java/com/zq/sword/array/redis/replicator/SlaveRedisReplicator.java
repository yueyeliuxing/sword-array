package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.redis.interceptor.CommandInterceptor;
import com.zq.sword.array.redis.replicator.listener.RedisReplicatorListener;
import com.zq.sword.array.tasks.Actuator;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public interface SlaveRedisReplicator extends Actuator {

    /**
     * 添加命令拦截器
     * @param interceptor
     */
    void addCommandInterceptor(CommandInterceptor interceptor);

    /**
     * 添加复制监听器
     * @param redisReplicatorListener
     */
    void addRedisReplicatorListener(RedisReplicatorListener redisReplicatorListener);

}
