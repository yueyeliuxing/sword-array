package com.zq.sword.array.data.rqueue.service.impl;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.node.NodeServerId;
import com.zq.sword.array.common.service.AbstractService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.domain.DataItem;
import com.zq.sword.array.data.rqueue.manager.FileDataIndexManager;
import com.zq.sword.array.data.rqueue.manager.FileDataItemManager;
import com.zq.sword.array.data.rqueue.manager.MemoryDataIndexManager;
import com.zq.sword.array.data.rqueue.manager.impl.FileDataIndexManagerImpl;
import com.zq.sword.array.data.rqueue.manager.impl.FileDataItemManagerImpl;
import com.zq.sword.array.data.rqueue.manager.impl.MemoryDataIndexManagerImpl;
import com.zq.sword.array.data.rqueue.service.DataIndexService;
import com.zq.sword.array.data.rqueue.service.DataItemService;
import com.zq.sword.array.data.rqueue.service.RightQueueService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 文件系统Right队列存储
 * @author: zhouqi1
 * @create: 2018-07-23 17:52
 **/
public class DefaultRightQueueService extends AbstractService implements RightQueueService {

    private DataItemService dataItemService;

    private DataIndexService dataIndexService;

    private Map<NodeServerId, DataEventListener<DataItem>> dataEventListeners;

    public DefaultRightQueueService() {
        dataEventListeners = new ConcurrentHashMap<>();
        dataItemService = new DefaultDataItemService();
        dataIndexService = new DefaultDataIndexService();
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        dataItemService.start(serviceConfig);
        dataIndexService.start(serviceConfig);
    }

    @Override
    public void registerDataItemListener(NodeServerId nodeServerId, DataEventListener<DataItem> dataItemDataEventListener) {
        dataEventListeners.put(nodeServerId, dataItemDataEventListener);
    }

    @Override
    public void push(DataItem dataItem) {
        DataIndex dataIndex = dataItemService.addDataItem(dataItem);
        dataIndexService.addDataIndex(dataIndex);

        //数据添加通知监听器
        if(dataEventListeners != null && !dataEventListeners.isEmpty()){
            for(NodeServerId nodeServerId : dataEventListeners.keySet()){
                DataEventListener<DataItem> dataItemDataEventListener = dataEventListeners.get(nodeServerId);
                DataEvent<DataItem> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_DATA_ITEM_CHANGE);
                dataEvent.setData(dataItem);
                dataItemDataEventListener.listen(dataEvent);
            }
        }
    }

    @Override
    public List<DataItem> pollAfterId(Long id) {
        return pollAfterId(id, null);
    }

    @Override
    public List<DataItem> pollAfterId(Long id, Integer maxNum) {
        DataIndex dataIndex = dataIndexService.getDataIndex(id);
        return dataItemService.listDataItemAfterIndex(dataIndex, maxNum);
    }

}
