package com.zq.sword.array.data.rqueue.service.impl;

import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.domain.DataItem;
import com.zq.sword.array.data.rqueue.manager.FileDataItemManager;
import com.zq.sword.array.data.rqueue.manager.impl.FileDataItemManagerImpl;
import com.zq.sword.array.data.rqueue.service.DataItemService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 默认的数据索引服务实现
 * @author: zhouqi1
 * @create: 2018-07-31 20:12
 **/
public class DefaultDataItemService extends AbstractTaskService implements DataItemService {

    private Long lastDataItemId;

    private FileDataItemManager fileDataItemManager;

    private int dataItemFileRetainDays = 7;

    @Override
    public void start(ServiceConfig serviceConfig) {
        String dataItemFilePath = serviceConfig.getProperty(NodeServerConfigKey.T_RIGHT_DATA_ITEM_FILE_PATH);
        fileDataItemManager = new FileDataItemManagerImpl(dataItemFilePath);

        lastDataItemId = fileDataItemManager.getLastDataItemId();

        //初始化任务
        initTasks();
    }

    private void initTasks(){
        //添加定时删除数据文件
        loadTimedTask(()->{
            fileDataItemManager.removeDataItem(DateUtil.minusDays(new Date(), dataItemFileRetainDays));
        }, 1, TimeUnit.DAYS);
    }


    @Override
    public Long getLastDataItemId() {
        return lastDataItemId;
    }

    @Override
    public DataIndex addDataItem(DataItem dataItem) {
        lastDataItemId = dataItem.getId();
        return fileDataItemManager.addDataItem(dataItem);
    }

    @Override
    public List<DataItem> listDataItemAfterIndex(DataIndex dataIndex, Integer num) {
        return fileDataItemManager.listDataItemAfterIndex(dataIndex, num);
    }
}
