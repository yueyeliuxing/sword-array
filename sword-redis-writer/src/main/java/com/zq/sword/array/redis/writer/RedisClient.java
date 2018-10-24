package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.Sword;

/**
 * @program: sword-array
 * @description: redis 客户端
 * @author: zhouqi1
 * @create: 2018-10-18 20:51
 **/
public interface RedisClient<T extends Sword> {

    /**
     * 写入数据
     * @param data
     * @return
     */
    boolean write(T data);

    /**
     * 关闭
     */
    void close();
}
