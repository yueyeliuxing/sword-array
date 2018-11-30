package com.zq.sword.array.redis.writer;

import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 数据写入器
 * @author: zhouqi1
 * @create: 2018-10-23 21:43
 **/
public class SwordRedisCommandWriter implements RedisCommandWriter {

    private Logger logger = LoggerFactory.getLogger(SwordRedisCommandWriter.class);

    private SwordRedisCommandBackgroundExecutor swordRedisCommandBackgroundExecutor;

    private DataQueue<SwordData> dataQueue;

    private RedisClient<SwordCommand> redisClient;

    private volatile boolean SUCCESS_TAG = true;

    private SwordRedisCommandWriter(RedisConfig redisConfig, DataQueue<SwordData> dataQueue) {
        redisClient = new SwordRedisClient(redisConfig);
        swordRedisCommandBackgroundExecutor = new SwordRedisCommandBackgroundExecutor();
        this.dataQueue = dataQueue;
    }

    public static class SwordRedisCommandWriterBuilder{
        private RedisConfig redisConfig;
        private DataQueue<SwordData> dataQueue;

        public static SwordRedisCommandWriterBuilder create(){
            return new SwordRedisCommandWriterBuilder();
        }

        public SwordRedisCommandWriterBuilder config(RedisConfig redisConfig){
            this.redisConfig = redisConfig;
            return this;
        }

        public SwordRedisCommandWriterBuilder bindingDataSource(DataQueue<SwordData> dataQueue){
            this.dataQueue = dataQueue;
            return this;
        }

        public SwordRedisCommandWriter build(){
            return new SwordRedisCommandWriter(redisConfig, dataQueue);
        }
    }

   /* private RedisConfig getRedisConfig(SwordConfig swordConfig) {
        String host = swordConfig.getProperty("redis.host");
        String port = swordConfig.getProperty("redis.port");
        String pass = swordConfig.getProperty("redis.pass");
        String timeout = swordConfig.getProperty("redis.timeout");
        String maxIdle = swordConfig.getProperty("redis.maxIdle");
        String maxTotal = swordConfig.getProperty("redis.maxTotal");
        String maxWaitMillis = swordConfig.getProperty("redis.maxWaitMillis");
        String testOnBorrow = swordConfig.getProperty("redis.testOnBorrow");
        return new RedisConfig(host, port, pass, timeout, maxIdle, maxTotal, maxWaitMillis, testOnBorrow);
    }*/

    @Override
    public void start() {

        if(dataQueue == null) {
            logger.error("dataQueue is not connect success");
            throw new NullPointerException("dataQueue");
        }

        //初始化任务
        initTasks();
    }

    private void initTasks() {
        swordRedisCommandBackgroundExecutor.execute(()->{
            while (SUCCESS_TAG && !Thread.currentThread().isInterrupted()){
                SwordData swordData = dataQueue.poll();
                if(swordData != null){
                    logger.error("get dataQueue data {}", swordData);
                    redisClient.write(swordData.getValue());
                }else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("current thread interrupt");
                    }
                }
            }
        });
    }

    @Override
    public void close() {
        SUCCESS_TAG = false;
        redisClient.close();
    }

}
