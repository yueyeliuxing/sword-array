package com.zq.sword.array.data.lqueue.manager.impl;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.data.lqueue.domain.DataItem;
import com.zq.sword.array.data.lqueue.helper.DataItemHelper;
import com.zq.sword.array.data.lqueue.manager.DataQueuePersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: 队列持久化管理
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class DataQueuePersistenceManagerImpl implements DataQueuePersistenceManager {

    private Logger logger = LoggerFactory.getLogger(DataQueuePersistenceManagerImpl.class);

    private static final String DATA_ITEM_FILE_SUFFIX = ".data";

    private static final String DATA_ITEM_FILE_TEMP_SUFFIX = ".temp";

    private static final String DATA_ITEM_DELETE_TAG = "DELETE_TAG";

    /**
     * 数据文件路径
     */
    private String dataItemFilePath;

    public DataQueuePersistenceManagerImpl(String dataItemFilePath) {
        this.dataItemFilePath = dataItemFilePath;
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getDataItemFilePath(String fileName){
        return String.format("%s/%s%s", dataItemFilePath, fileName, DATA_ITEM_FILE_SUFFIX);
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getDataItemFileTempPath(String fileName){
        return String.format("%s/%s%s", dataItemFilePath, fileName, DATA_ITEM_FILE_TEMP_SUFFIX);
    }

    @Override
    public void resetDataItem(List<DataItem> dataItems) {
        if(dataItems != null && !dataItems.isEmpty()){
            Set<Long> itemIds = new HashSet<>();
            for(DataItem dataItem : dataItems){
                Long itemId = dataItem.getId();
                if(itemIds.contains(itemId)){
                    continue;
                }
                String fileName = DateUtil.formatDate(new Date(dataItem.getTimestamp()), DateUtil.YYYY_MM_DD);
                File dataItemFile = new File(getDataItemFilePath(fileName));
                File dataItemTempFile = new File(getDataItemFileTempPath(fileName));
                if(dataItemTempFile.exists()){
                    dataItemTempFile.delete();
                }
                dataItemFile.renameTo(dataItemTempFile);

                String dataLine = DataItemHelper.getDataLine(dataItem);
                FileUtil.appendLine(dataItemFile, dataLine);
                itemIds.add(itemId);
            }
        }
    }

    @Override
    public void persistenceDataItem(DataItem...  dataItems) {
        if(dataItems != null && dataItems.length > 0){
            Set<Long> itemIds = new HashSet<>();
            for(DataItem dataItem : dataItems){
                Long itemId = dataItem.getId();
                if(itemIds.contains(itemId)){
                    continue;
                }
                String fileName = DateUtil.formatDate(new Date(dataItem.getTimestamp()), DateUtil.YYYY_MM_DD);
                String dataItemFilePath = getDataItemFilePath(fileName);
                File dataItemFile = new File(dataItemFilePath);
                String dataLine = DataItemHelper.getDataLine(dataItem);
                FileUtil.appendLine(dataItemFile, dataLine);
                itemIds.add(itemId);
            }
        }
    }

    @Override
    public List<DataItem> loadDataItem() {
        List<DataItem> dataItems = new ArrayList<>();
        File[] childFiles = FileUtil.listChildFile(dataItemFilePath, DATA_ITEM_FILE_SUFFIX);
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles){
                List<String> dataLines = FileUtil.readLines(childFile);
                if(dataLines != null && !dataLines.isEmpty()){
                    List<DataItem> childDataIndex = dataLines.stream().map(DataItemHelper::getDataItem)
                            .filter(c->!c.getValue().equals(DATA_ITEM_DELETE_TAG)).collect(Collectors.toList());
                    dataItems.addAll(childDataIndex);
                }
            }
        }
        return dataItems;
    }

    @Override
    public void removeDataItem(DataItem dataItem) {
        dataItem.setValue(DATA_ITEM_DELETE_TAG);
        String fileName = DateUtil.formatDate(new Date(dataItem.getTimestamp()), DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(fileName);
        File dataItemFile = new File(dataItemFilePath);
        String dataLine = DataItemHelper.getDataLine(dataItem);
        FileUtil.appendLine(dataItemFile, dataLine);
    }
}
