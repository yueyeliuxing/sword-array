package com.zq.sword.array.transfer.server.container;

import com.zq.sword.array.common.service.AbstractContainer;
import com.zq.sword.array.transfer.server.TransferContainerContainer;
import com.zq.sword.array.transfer.server.service.TransferServerService;
import com.zq.sword.array.transfer.server.service.impl.DefaultTransferServerService;

/**
 * @program: sword-array
 * @description: 传输服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:51
 **/
public class DefaultTransferContainerContainer extends AbstractContainer implements TransferContainerContainer {

    public DefaultTransferContainerContainer() {
        registerService(TransferServerService.class, new DefaultTransferServerService());
    }
}
