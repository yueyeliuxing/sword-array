package com.zq.sword.array.redis.writer;


import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;

/**
 * @program: sword-array
 * @description: rdis 命令写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:42
 **/
public interface RedisCommandWriter {

    /**
     * 开启写入器
     */
    void start();

    /**
     *  关闭写入器
     */
    void close();
}
