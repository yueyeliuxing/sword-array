package com.zq.sword.array.metadata.container;

import com.zq.sword.array.common.service.AbstractServer;
import com.zq.sword.array.metadata.MetadataServiceContainer;
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
public class DefaultMetadataServiceContainer extends AbstractServer implements MetadataServiceContainer {

    public DefaultMetadataServiceContainer() {
        this(new ZkDataConfService());
    }

    public DefaultMetadataServiceContainer(DataConfService dataConfService) {
        registerService(NodeConfService.class, new DefaultNodeConfService(dataConfService));
        registerService(NamingConfService.class, new DefaultNamingConfService(dataConfService));
        registerService(DataConsumptionConfService.class, new DefaultDataConsumptionConfService(dataConfService));
    }
}
