package com.zq.sword.array.redis.replicator;


import com.zq.sword.array.data.Sword;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public interface SlaveRedisReplicator<T extends Sword> {

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void stop();


}
