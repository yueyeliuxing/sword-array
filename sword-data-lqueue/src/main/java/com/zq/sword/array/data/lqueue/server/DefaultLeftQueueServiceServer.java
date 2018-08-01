package com.zq.sword.array.data.lqueue.server;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.data.lqueue.LeftQueueServiceServer;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.data.lqueue.service.impl.DefaultLeftQueueService;

/**
 * @program: sword-array
 * @description: T-Left队列服务
 * @author: zhouqi1
 * @create: 2018-07-23 19:31
 **/
public class DefaultLeftQueueServiceServer extends AbstractServer implements LeftQueueServiceServer {

    public DefaultLeftQueueServiceServer() {
        registerService(LeftQueueService.class, new DefaultLeftQueueService());
    }
}
