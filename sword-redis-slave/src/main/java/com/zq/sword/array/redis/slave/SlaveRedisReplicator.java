package com.zq.sword.array.redis.slave;


import com.zq.sword.array.common.data.Sword;
import com.zq.sword.array.common.data.SwordData;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.data.rqueue.RightRandomQueue;

/**
 * @program: sword-array
 * @description: redis 复制服务
 * @author: zhouqi1
 * @create: 2018-10-10 14:51
 **/
public interface SlaveRedisReplicator<T extends Sword> {

    /**
     * 绑定目的数据源
     * @param rightRandomQueue
     */
    void bindingTargetDataSource(RightRandomQueue<SwordData> rightRandomQueue);

    /**
     * 开启
     */
    void start();

    /**
     * 关闭
     */
    void stop();


}
