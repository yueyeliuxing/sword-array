package com.zq.sword.array.data.lqueue;


import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.Sword;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据存储队列服务
 * @author: zhouqi1
 * @create: 2018-08-01 11:47
 **/
public interface LeftOrderlyQueue<T> extends DataQueue<T> {

    /**
     * 绑定桥梁
     * @param dataCycleDisposeBridge
     */
    void bindingDataCycleDisposeBridge(DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge);

}
