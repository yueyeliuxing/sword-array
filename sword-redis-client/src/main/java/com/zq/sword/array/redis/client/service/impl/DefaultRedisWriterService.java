package com.zq.sword.array.redis.client.service.impl;

import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.common.task.Task;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.redis.client.helper.JedisClient;
import com.zq.sword.array.redis.client.service.RedisWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class DefaultRedisWriterService extends AbstractTaskService implements RedisWriterService {

    private Logger logger = LoggerFactory.getLogger(DefaultRedisWriterService.class);

    private JedisClient jedisClient;

    private LeftQueueService getLeftQueueService(){
        return ServiceContext.getInstance().findService(LeftQueueService.class);
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        //String redisConnectAddr = serviceConfig.getProperty(NodeServerConfigKey.REDIS_CONNECT_ADDR);
        jedisClient = new JedisClient();
        jedisClient.initJedis(null);

        initTasks();
    }

    private void initTasks() {
        loadTask(new Task() {
            @Override
            public void execute() {
                LeftQueueService leftQueueService = getLeftQueueService();
                while (true){
                    DataItem dataItem = leftQueueService.peekDataItem();
                    String value = dataItem.getValue();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    byteBuffer.put(value.getBytes());
                    byteBuffer.reset();

                    byte type = byteBuffer.get();
                    if(type == 1){
                        int keyLen = byteBuffer.getInt();
                        byte[] keyBytes = new byte[keyLen];
                        byteBuffer.get(keyBytes);
                        int valueLen = byteBuffer.getInt();
                        byte[] valueBytes = new byte[valueLen];
                        byteBuffer.get(valueBytes);
                        try{
                            jedisClient.saveValueByKey(0, keyBytes, valueBytes, 0);
                            leftQueueService.removeDataItem(dataItem);
                            leftQueueService.addConsumedDataItem(dataItem);
                        }catch (Exception e){
                            logger.error("saveValueByKey error");
                        }
                    }
                }
            }
        });
    }
}
