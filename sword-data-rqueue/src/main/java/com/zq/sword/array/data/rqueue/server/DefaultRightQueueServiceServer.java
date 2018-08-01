package com.zq.sword.array.data.rqueue.server;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.data.rqueue.RightQueueServiceServer;
import com.zq.sword.array.data.rqueue.service.RightQueueService;
import com.zq.sword.array.data.rqueue.service.impl.DefaultRightQueueService;

/**
 * @program: sword-array
 * @description: T-Right队列服务
 * @author: zhouqi1
 * @create: 2018-07-23 19:31
 **/
public class DefaultRightQueueServiceServer extends AbstractServer implements RightQueueServiceServer {

    public DefaultRightQueueServiceServer() {
        registerService(RightQueueService.class, new DefaultRightQueueService());
    }
}
