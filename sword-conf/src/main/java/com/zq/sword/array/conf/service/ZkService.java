package com.zq.sword.array.conf.service;

import com.zq.sword.array.common.service.Service;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;

import java.util.List;

/**
 * @program: sword-array
 * @description: Zk服务
 * @author: zhouqi1
 * @create: 2018-07-24 10:26
 **/
public interface ZkService extends Service {

    void registerChildChangeListener(String parentPath, IZkChildListener childListener);

    void registerDataChangeListener(String path, IZkDataListener dataListener);

    void createEphemeral(String path);

    void createEphemeral(String path, Object data);

    void createPersistent(String path);

    void createPersistent(String path, Object data);

    void writeData(String path, String value);

    String readData(String path);

    boolean exists(String path);

    List<String> getChildren(String path);

    boolean delete(String path);
}
