package com.zq.sword.array.backup.slave.container;

import com.zq.sword.array.backup.slave.SlaveBackupContainer;
import com.zq.sword.array.backup.slave.service.SlaveBackupDataService;
import com.zq.sword.array.backup.slave.service.impl.DefaultSlaveBackupDataService;
import com.zq.sword.array.common.service.AbstractContainer;

/**
 * @program: sword-array
 * @description: slave备份容器
 * @author: zhouqi1.
 * @create: 2018-08-04 12:03
 **/
public class DefaultSlaveBackupContainer extends AbstractContainer implements SlaveBackupContainer {

    public DefaultSlaveBackupContainer() {
        registerService(SlaveBackupDataService.class, new DefaultSlaveBackupDataService());
    }
}
