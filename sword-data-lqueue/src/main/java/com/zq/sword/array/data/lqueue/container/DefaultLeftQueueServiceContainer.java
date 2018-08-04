package com.zq.sword.array.data.lqueue.container;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.data.lqueue.LeftQueueServiceContainer;
import com.zq.sword.array.data.lqueue.service.LeftQueueService;
import com.zq.sword.array.data.lqueue.service.impl.DefaultLeftQueueService;

/**
 * @program: sword-array
 * @description: T-Left队列服务
 * @author: zhouqi1
 * @create: 2018-07-23 19:31
 **/
public class DefaultLeftQueueServiceContainer extends AbstractServer implements LeftQueueServiceContainer {

    public DefaultLeftQueueServiceContainer() {
        registerService(LeftQueueService.class, new DefaultLeftQueueService());
    }
}
