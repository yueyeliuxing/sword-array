package com.zq.sword.array.data.rqueue.container;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.data.rqueue.RightQueueServiceContainer;
import com.zq.sword.array.data.rqueue.service.RightQueueService;
import com.zq.sword.array.data.rqueue.service.impl.DefaultRightQueueService;

/**
 * @program: sword-array
 * @description: T-Right队列服务
 * @author: zhouqi1
 * @create: 2018-07-23 19:31
 **/
public class DefaultRightQueueServiceContainer extends AbstractServer implements RightQueueServiceContainer {

    public DefaultRightQueueServiceContainer() {
        registerService(RightQueueService.class, new DefaultRightQueueService());
    }
}
