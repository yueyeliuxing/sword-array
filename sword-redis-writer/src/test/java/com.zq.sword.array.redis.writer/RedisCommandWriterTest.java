package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.bitcask.BitcaskLeftOrderlyQueue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedisCommandWriterTest {

    private RedisCommandWriter redisCommandWriter;

    @Before
    public void setUp() throws Exception {
        LeftOrderlyQueue<SwordData> leftOrderlyQueue = new BitcaskLeftOrderlyQueue("E:\\sword\\left\\data");
        redisCommandWriter = SwordRedisCommandWriter.SwordRedisCommandWriterBuilder.create()
                .config(getRedisConfig())
                .bindingDataSource(leftOrderlyQueue)
                .build();
    }

    private RedisConfig getRedisConfig() {
        String host = "127.0.0.1";
        String port = "6379";
        String pass = null;
        String timeout = "100000";
        String maxIdle = "10";
        String maxTotal = "100";
        String maxWaitMillis = "30000";
        String testOnBorrow =  null;
        return new RedisConfig(host, port, pass, timeout, maxIdle, maxTotal, maxWaitMillis, testOnBorrow);
    }

    @Test
    public void start() throws Exception{
        redisCommandWriter.start();
        System.in.read();
    }

    @Test
    public void close() {
        redisCommandWriter.close();
    }
}