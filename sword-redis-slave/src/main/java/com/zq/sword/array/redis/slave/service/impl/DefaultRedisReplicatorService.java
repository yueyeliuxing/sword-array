package com.zq.sword.array.redis.slave.service.impl;

import com.moilioncircle.redis.replicator.RedisReplicator;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.EventListener;
import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.data.rqueue.domain.DataItem;
import com.zq.sword.array.data.rqueue.service.RightQueueService;
import com.zq.sword.array.redis.slave.helper.CommandSerializationHelper;
import com.zq.sword.array.redis.slave.service.RedisReplicatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public class DefaultRedisReplicatorService extends AbstractTaskService implements RedisReplicatorService {

    private Logger logger = LoggerFactory.getLogger(DefaultRedisReplicatorService.class);

    private Replicator replicator;

    private RightQueueService getRightQueueService(){
        return ServiceContext.getInstance().findService(RightQueueService.class);
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        String redisConnectAddr = serviceConfig.getProperty(NodeServerConfigKey.REDIS_CONNECT_ADDR);

        //初始化redis复制器
        initRedisReplicator(redisConnectAddr);
    }

    private void initRedisReplicator(String redisConnectAddr){
        try {
            if(replicator != null){
                replicator.close();
            }
            replicator = new RedisReplicator(String.format("redis://%s", redisConnectAddr));
            replicator.addEventListener(new EventListener() {
                @Override
                public void onEvent(Replicator replicator, Event event) {
                    if(event instanceof Command){
                        Command command = (Command) event;

                        RightQueueService rightQueueService = getRightQueueService();
                        DataItem dataItem = new DataItem();
                        dataItem.setId(1L);
                        dataItem.setValue(CommandSerializationHelper.serializeCommand(command));
                        dataItem.setTimestamp(System.currentTimeMillis());
                        rightQueueService.push(dataItem);
                    }
                }
            });
            replicator.open();
        }catch (Exception e){
            logger.error("initRedisReplicator error ", e);
            throw new RuntimeException(e);
        }
    }
}
