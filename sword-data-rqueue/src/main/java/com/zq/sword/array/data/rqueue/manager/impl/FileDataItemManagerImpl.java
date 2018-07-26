package com.zq.sword.array.data.rqueue.manager.impl;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.domain.DataItem;
import com.zq.sword.array.data.rqueue.helper.DataItemHelper;
import com.zq.sword.array.data.rqueue.manager.FileDataItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: 文件数据索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class FileDataItemManagerImpl implements FileDataItemManager {

    private Logger logger = LoggerFactory.getLogger(FileDataItemManagerImpl.class);

    private static final String DATA_ITEM_FILE_SUFFIX = ".data";

    /**
     * 数据文件路径
     */
    private String dataItemFilePath;

    public FileDataItemManagerImpl(String dataItemFilePath) {
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
     * 添加数据
     * @param dataItem
     */
    @Override
    public DataIndex addDataItem(DataItem dataItem){
        DataIndex dataIndex = new DataIndex();
        String fileName = DateUtil.formatDate(new Date(dataItem.getTimestamp()), DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(fileName);
        File dataItemFile = new File(dataItemFilePath);

        long pos = dataItemFile.length();
        dataIndex.setValuePosition(pos+1);

        String dataLine = DataItemHelper.getDataLine(dataItem);
        FileUtil.appendLine(dataItemFile, dataLine);

        dataIndex.setDataId(dataItem.getId());
        dataIndex.setFileId(fileName);
        dataIndex.setTimestamp(dataItem.getTimestamp());
        dataIndex.setValueLength(Long.valueOf(dataLine.length()));
        return dataIndex;
    }

    /**
     * 获取指定索引的数据
     * @param dataIndex
     * @return
     */
    @Override
    public List<DataItem> listDataItemAfterIndex(DataIndex dataIndex){
        return listDataItemAfterIndex(dataIndex, null);
    }

    /**
     * 获取指定索引的数据
     * @param dataIndex
     * @return
     */
    @Override
    public List<DataItem> listDataItemAfterIndex(DataIndex dataIndex, Integer num){
        List<DataItem> dataIndices = new ArrayList<>();
        long valuePos = dataIndex.getValuePosition();
        String startDateValue = DateUtil.formatDate(new Date(dataIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(startDateValue);
        List<String> dataLines = FileUtil.readLines(dataItemFilePath, valuePos, num);
        if(dataLines != null && !dataLines.isEmpty()){
            dataIndices.addAll(dataLines.stream().map(DataItemHelper::getDataItem).collect(Collectors.toList()));
        }
        return dataIndices;
    }

    /***
     * 删除指定日期的数据
     * @param date
     */
    @Override
    public void removeDataIndex(Date date){
        String dateValue = DateUtil.formatDate(date, DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(dateValue);
        File dataItemFile = new File(dataItemFilePath);
        dataItemFile.delete();
    }
}
