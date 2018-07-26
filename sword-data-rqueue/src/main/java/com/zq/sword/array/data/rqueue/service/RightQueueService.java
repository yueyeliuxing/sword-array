package com.zq.sword.array.data.rqueue.service;

import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.Service;
import com.zq.sword.array.data.rqueue.domain.DataItem;

import java.util.List;

/**
 * @program: sword-array
 * @description: 数据服务
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
public interface RightQueueService extends Service {

    /**
     * 注册数据变化监听器
     * @param nodeServerId
     * @param dataItemDataEventListener
     */
    void registerDataItemListener(NodeServerId nodeServerId, DataEventListener<DataItem> dataItemDataEventListener);

    /**
     * 添加数据项
     * @param dataItem
     */
    void push(DataItem dataItem);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @return 数据项
     */
    List<DataItem> pollAfterId(Long id);

    /**
     * 获取指定ID之后的数据项
     * @param id 数据项ID
     * @param maxNum 最大获取的数目
     * @return 数据项
     */
    List<DataItem> pollAfterId(Long id, Integer maxNum);
}
