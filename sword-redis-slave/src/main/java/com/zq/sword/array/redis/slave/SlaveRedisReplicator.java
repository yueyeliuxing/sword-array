package com.zq.sword.array.redis.slave;


import com.zq.sword.array.common.data.Sword;
import com.zq.sword.array.common.event.DataEventListener;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public interface SlaveRedisReplicator<T extends Sword> {

    /**
     * 添加数据谨监听器
     * @param listener
     * @return
     */
    boolean addEventListener(DataEventListener<T> listener);

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void stop();

    /**
     * 重启
     */
    void reset();


}
