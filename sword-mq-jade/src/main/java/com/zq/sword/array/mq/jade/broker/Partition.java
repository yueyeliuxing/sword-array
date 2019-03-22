package com.zq.sword.array.mq.jade.broker;

import com.zq.sword.array.stream.io.Resource;
import com.zq.sword.array.stream.io.ex.InputStreamOpenException;
import com.zq.sword.array.stream.io.ex.OutputStreamOpenException;
import com.zq.sword.array.stream.io.object.ObjectInputStream;
import com.zq.sword.array.stream.io.object.ObjectOutputStream;

/**
 * @program: sword-array
 * @description: 数据块
 * @author: zhouqi1
 * @create: 2019-01-16 10:30
 **/
public interface Partition extends Resource {

    /**
     * id
     * @return
     */
    long id();

    /**
     * 标签
     * @return
     */
    String tag();

    /**
     * 名称
     * @return
     */
    String name();

    /**
     * 路径
     * @return
     */
    String path();

    /**
     * topic 配置
     * @return
     */
    String topic();

    @Override
    ObjectInputStream openInputStream() throws InputStreamOpenException;

    @Override
    ObjectOutputStream openOutputStream() throws OutputStreamOpenException;

    /**
     * 是否关闭
     * @return
     */
    boolean isClose();
}
