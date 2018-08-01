package com.zq.sword.array.metadata.server;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.NodeMetadataInfo;
import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.metadata.MetadataServiceServer;
import com.zq.sword.array.metadata.service.DataConsumptionConfService;
import com.zq.sword.array.metadata.service.NamingConfService;
import com.zq.sword.array.metadata.service.NodeConfService;
import com.zq.sword.array.metadata.service.DataConfService;
import com.zq.sword.array.metadata.service.impl.ZkDataConfService;
import com.zq.sword.array.metadata.service.impl.DefaultDataConsumptionConfService;
import com.zq.sword.array.metadata.service.impl.DefaultNamingConfService;
import com.zq.sword.array.metadata.service.impl.DefaultNodeConfService;

/**
 * @program: sword-array
 * @description: 配置服务
 * @author: zhouqi1
 * @create: 2018-07-23 20:25
 **/
public class DefaultMetadataServiceServer extends AbstractServer implements MetadataServiceServer {

    public DefaultMetadataServiceServer() {
        this(new ZkDataConfService());
    }

    public DefaultMetadataServiceServer(DataConfService dataConfService) {
        registerService(NodeConfService.class, new DefaultNodeConfService(dataConfService));
        registerService(NamingConfService.class, new DefaultNamingConfService(dataConfService));
        registerService(DataConsumptionConfService.class, new DefaultDataConsumptionConfService(dataConfService));
    }
}
