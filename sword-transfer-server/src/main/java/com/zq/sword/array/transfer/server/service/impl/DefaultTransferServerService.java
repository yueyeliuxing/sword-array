package com.zq.sword.array.transfer.server.service.impl;

import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.service.ServiceContext;
import com.zq.sword.array.common.service.TaskService;
import com.zq.sword.array.common.utils.IPUtil;
import com.zq.sword.array.netty.server.DefaultTransferServer;
import com.zq.sword.array.netty.server.TransferServer;
import com.zq.sword.array.transfer.server.service.TransferServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: sword-array
 * @description: 服务
 * @author: zhouqi1
 * @create: 2018-08-04 10:57
 **/
public class DefaultTransferServerService extends AbstractTaskService implements TransferServerService {

    private Logger logger = LoggerFactory.getLogger(DefaultTransferServerService.class);

    private TransferServer transferServer;

    @Override
    public void start(ServiceConfig serviceConfig) {
        int port = serviceConfig.getProperty(NodeServerConfigKey.NODE_SERVER_START_PORT, Integer.class);
        String ip = IPUtil.getServerIp();
        if(ip == null){
            logger.error("获取IP失败，服务停止启动");
            throw new NullPointerException("ip");
        }
    }

    private void startServer(String ip, int port){
        transferServer = new DefaultTransferServer(ip, port);
        transferServer.start();
    }
}
