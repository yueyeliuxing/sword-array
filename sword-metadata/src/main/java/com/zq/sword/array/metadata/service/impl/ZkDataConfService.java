package com.zq.sword.array.metadata.service.impl;

import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.metadata.service.DataConfService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;


/**
 * @program: sword-array
 * @description: Zk服务
 * @author: zhouqi1
 * @create: 2018-07-24 10:26
 **/
public class ZkDataConfService extends AbstractService implements DataConfService {

    private ZkClient zkClient;


    @Override
    public void start(ServiceConfig serviceConfig) {
        start();
        String connectAddr = serviceConfig.getProperty(NodeServerConfigKey.ZK_CONNECT_ADDR);
        int sessionTimeOut = serviceConfig.getProperty(NodeServerConfigKey.ZK_CONNECT_TIMEOUT, Integer.class);
        zkClient = new ZkClient(new ZkConnection(connectAddr), sessionTimeOut);
    }

    @Override
    public void registerChildChangeListener(String parentPath, IZkChildListener childListener) {
        zkClient.subscribeChildChanges(parentPath, childListener);
    }

    @Override
    public void registerDataChangeListener(String path, IZkDataListener dataListener) {
        zkClient.subscribeDataChanges(path, dataListener);
    }


    @Override
    public void createEphemeral(String path) {
        zkClient.createEphemeral(path);
    }

    @Override
    public void createEphemeral(String path, Object data) {
        zkClient.createEphemeral(path, data);
    }

    @Override
    public void createPersistent(String path) {
        zkClient.createPersistent(path, true);
    }

    @Override
    public void createPersistent(String path, Object data) {
        zkClient.createPersistent(path, data);
    }

    @Override
    public void writeData(String path, String value) {
        zkClient.writeData(path, value);
    }

    @Override
    public String readData(String path) {
        return zkClient.readData(path);
    }

    @Override
    public boolean exists(String path) {
        return zkClient.exists(path);
    }

    @Override
    public List<String> getChildren(String path) {
        return zkClient.getChildren(path);
    }

    @Override
    public boolean delete(String path) {
        return zkClient.delete(path);
    }


}
