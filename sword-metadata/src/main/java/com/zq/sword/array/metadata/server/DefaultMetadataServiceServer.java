package com.zq.sword.array.metadata.server;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.metadata.MetadataServiceServer;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
import com.zq.sword.array.metadata.service.NamingConfService;
import com.zq.sword.array.metadata.service.NodeConfService;
import com.zq.sword.array.metadata.service.DataConfService;
import com.zq.sword.array.metadata.service.impl.ZkDataConfService;
import com.zq.sword.array.metadata.service.impl.ZkDataConsumptionConfService;
import com.zq.sword.array.metadata.service.impl.ZkNamingConfService;
import com.zq.sword.array.metadata.service.impl.ZkNodeConfService;

/**
 * @program: sword-array
 * @description: 配置服务
 * @author: zhouqi1
 * @create: 2018-07-23 20:25
 **/
public class DefaultMetadataServiceServer extends AbstractServer implements MetadataServiceServer {

    public DefaultMetadataServiceServer() {
        DataConfService dataConfService = new ZkDataConfService();
        registerService(NodeConfService.class, new ZkNodeConfService(dataConfService));
        registerService(NamingConfService.class, new ZkNamingConfService(dataConfService));
        registerService(DataConsumptionConfService.class, new ZkDataConsumptionConfService(dataConfService));
    }
}
