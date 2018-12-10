package com.zq.sword.array.data.rqueue;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.data.DataQueue;
import com.zq.sword.array.data.Sword;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据服务
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
public interface RightRandomQueue<T> extends DataQueue<T> {

    /**
     * 绑定处理循环处理
     * @param dataCycleDisposeBridge
     */
    void bindingDataCycleDisposeBridge(DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge);

}
