package com.zq.sword.array.transfer.client.container;

import com.zq.sword.array.common.service.AbstractContainer;
import com.zq.sword.array.transfer.client.TransferClientContainer;
import com.zq.sword.array.transfer.client.service.TransferClientService;
import com.zq.sword.array.transfer.client.service.impl.DefaultTransferClientService;

/**
 * @program: sword-array
 * @description: 获取数据客户端服务容器
 * @author: zhouqi1
 * @create: 2018-08-01 17:23
 **/
public class DefaultTransferClientContainer extends AbstractContainer implements TransferClientContainer {
    public DefaultTransferClientContainer() {
        registerService(TransferClientService.class, new DefaultTransferClientService());
    }
}
