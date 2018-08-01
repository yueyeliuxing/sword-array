package com.zq.sword.array.data.rqueue.service.impl;

import com.zq.sword.array.common.node.NodeServerConfigKey;
import com.zq.sword.array.common.service.AbstractTaskService;
import com.zq.sword.array.common.service.ServiceConfig;
import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.manager.FileDataIndexManager;
import com.zq.sword.array.data.rqueue.manager.FileDataItemManager;
import com.zq.sword.array.data.rqueue.manager.MemoryDataIndexManager;
import com.zq.sword.array.data.rqueue.manager.impl.FileDataIndexManagerImpl;
import com.zq.sword.array.data.rqueue.manager.impl.FileDataItemManagerImpl;
import com.zq.sword.array.data.rqueue.manager.impl.MemoryDataIndexManagerImpl;
import com.zq.sword.array.data.rqueue.service.DataIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 默认的数据索引服务实现
 * @author: zhouqi1
 * @create: 2018-07-31 20:12
 **/
public class DefaultDataIndexService extends AbstractTaskService implements DataIndexService {

    private Logger logger = LoggerFactory.getLogger(DefaultDataIndexService.class);

    private FileDataItemManager fileDataItemManager;

    private MemoryDataIndexManager memoryDataIndexManager;

    private FileDataIndexManager fileDataIndexManager;

    private ConcurrentLinkedQueue<DataIndex> addDataIndexQueue;

    /***
     * addDataIndexQueue 的最大数目
     */
    private static int MAX_ADD_DATA_INDEX_QUEUE = 10;

    /**
     * 添加到数据队列中的空闲时间 单位秒
     */
    private static int ADD_DATA_INDEX_QUEUE_FREE_TIME = 10000;

    private int dataItemFileRetainDays = 7;

    public DefaultDataIndexService() {
        addDataIndexQueue = new ConcurrentLinkedQueue();
    }

    @Override
    public void start(ServiceConfig serviceConfig) {
        MAX_ADD_DATA_INDEX_QUEUE = serviceConfig.getProperty(NodeServerConfigKey.T_RIGHT_DATA_INDEX_PERSISTENCE_NUM, Integer.class);
        ADD_DATA_INDEX_QUEUE_FREE_TIME = serviceConfig.getProperty(NodeServerConfigKey.T_RIGHT_DATA_INDEX_PERSISTENCE_FREE_TIME, Integer.class);

        String dataIndexFilePath = serviceConfig.getProperty(NodeServerConfigKey.T_RIGHT_DATA_INDEX_FILE_PATH);
        String dataItemFilePath = serviceConfig.getProperty(NodeServerConfigKey.T_RIGHT_DATA_ITEM_FILE_PATH);
        fileDataIndexManager = new FileDataIndexManagerImpl(dataIndexFilePath);
        fileDataItemManager = new FileDataItemManagerImpl(dataItemFilePath);
        memoryDataIndexManager = new MemoryDataIndexManagerImpl();

        initData();
        initTasks();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        List<DataIndex> dataIndices = fileDataIndexManager.listDataIndex();
        memoryDataIndexManager.load(dataIndices);
    }

    /**
     * 初始化任务
     */
    private void initTasks(){
        //定时内存索引持久化
        loadTask(()->{
            long prevTime = System.currentTimeMillis();
            while(true){
                List<DataIndex> dataIndices = new ArrayList<>();
                while (!addDataIndexQueue.isEmpty()){
                    dataIndices.add(addDataIndexQueue.poll());
                    if(dataIndices.size() >= MAX_ADD_DATA_INDEX_QUEUE || (System.currentTimeMillis() - prevTime >= ADD_DATA_INDEX_QUEUE_FREE_TIME)){
                        fileDataIndexManager.saveDataIndexFile(dataIndices);
                        dataIndices = new ArrayList<>();
                        prevTime = System.currentTimeMillis();
                    }
                }
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    logger.error("Thread.sleep 1000", e);
                }

            }
        });

        //定时删除索引文件
        loadTimedTask(()->{
            fileDataIndexManager.removeDataIndex(DateUtil.minusDays(new Date(), dataItemFileRetainDays));
            memoryDataIndexManager.removeDataIndex(DateUtil.minusDays(new Date(), dataItemFileRetainDays));
        }, 1, TimeUnit.DAYS);
    }

    @Override
    public void addDataIndex(DataIndex dataIndex) {
        memoryDataIndexManager.addDataIndex(dataIndex);

        //添加到新增数据索引队列
        addDataIndexQueue.add(dataIndex);
    }

    @Override
    public DataIndex getDataIndex(Long dataId) {
        return memoryDataIndexManager.getDataIndex(dataId);
    }


}
