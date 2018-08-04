package com.zq.sword.array.backup.master.service.impl;

import com.zq.sword.array.backup.master.handler.PushBackupDataHandler;
import com.zq.sword.array.backup.master.service.MasterBackupDataService;
import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.netty.server.DefaultTransferServer;
import com.zq.sword.array.netty.server.TransferServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: master备份数据服务
 * @author: zhouqi1
 * @create: 2018-08-04 16:09
 **/
public class DefaultMasterBackupDataService extends AbstractTaskService implements MasterBackupDataService {

    private Logger logger = LoggerFactory.getLogger(DefaultMasterBackupDataService.class);

    private TransferServer transferServer;

    private NodeServerId nodeServerId;

    @Override
    public void start(ServiceConfig serviceConfig) {
        int port = serviceConfig.getProperty(NodeServerConfigKey.NODE_SERVER_BACKUP_START_PORT, Integer.class);
        String ip = IPUtil.getServerIp();
        if(ip == null){
            logger.error("获取IP失败，服务停止启动");
            throw new NullPointerException("ip");
        }
        nodeServerId = serviceConfig.getId();

        //开启服务
        startServer(ip, port);
    }

    /**
     * 开启服务
     * @param ip
     * @param port
     */
    private void startServer(String ip, int port){
        transferServer = new DefaultTransferServer(ip, port);
        transferServer.registerTransferHandler(new PushBackupDataHandler());
        transferServer.start();
    }
}
