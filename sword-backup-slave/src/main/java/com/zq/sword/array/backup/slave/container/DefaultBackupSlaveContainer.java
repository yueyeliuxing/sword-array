package com.zq.sword.array.backup.slave.container;

import com.zq.sword.array.backup.slave.BackupSlaveContainer;
import com.zq.sword.array.backup.slave.service.BackupDataService;
import com.zq.sword.array.backup.slave.service.impl.DefaultBackupDataService;
import com.zq.sword.array.common.service.AbstractContainer;
import com.zq.sword.array.common.service.Container;

/**
 * @program: sword-array
 * @description: slave备份容器
 * @author: zhouqi1.
 * @create: 2018-08-04 12:03
 **/
public class DefaultBackupSlaveContainer extends AbstractContainer implements BackupSlaveContainer {

    public DefaultBackupSlaveContainer() {
        registerService(BackupDataService.class, new DefaultBackupDataService());
    }
}
