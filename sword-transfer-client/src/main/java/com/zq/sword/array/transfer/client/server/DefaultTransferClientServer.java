package com.zq.sword.array.transfer.client.server;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.common.service.Server;
import com.zq.sword.array.transfer.client.TransferClientServer;
import com.zq.sword.array.transfer.client.service.TransferClientService;
import com.zq.sword.array.transfer.client.service.impl.DefaultTransferClientService;

/**
 * @program: sword-array
 * @description: 获取数据客户端服务容器
 * @author: zhouqi1
 * @create: 2018-08-01 17:23
 **/
public class DefaultTransferClientServer extends AbstractServer implements TransferClientServer {
    public DefaultTransferClientServer() {
        registerService(TransferClientService.class, new DefaultTransferClientService());
    }
}
