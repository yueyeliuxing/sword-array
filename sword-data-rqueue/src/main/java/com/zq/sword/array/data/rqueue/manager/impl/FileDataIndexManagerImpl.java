package com.zq.sword.array.data.rqueue.manager.impl;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.helper.DataIndexHelper;
import com.zq.sword.array.data.rqueue.manager.FileDataIndexManager;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: 文件数据索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class FileDataIndexManagerImpl implements FileDataIndexManager {

    private static final String DATA_INDEX_FILE_SUFFIX = ".index";

    /**
     * 数据索引路径
     */
    private String dataIndexFilePath;

    public FileDataIndexManagerImpl(String dataIndexFilePath) {
        this.dataIndexFilePath = dataIndexFilePath;
    }

    /**
     * 获取当天数据索引文件路径
     * @return
     */
    private String getCurrentDayFilePath(){
        return String.format("%s/%s.index", dataIndexFilePath, DateUtil.getCurrentDate());
    }
    /**
     * 获取索引文件路径
     * @return
     */
    private String getDataIndexFilePath(String fileName){
        return String.format("%s/%s.index", dataIndexFilePath, fileName);
    }
    /**
     * 获取索引文件备份路径
     * @return
     */
    private String getDataIndexBackupFilePath(String fileName){
        return String.format("%s/%s.backup", dataIndexFilePath, fileName);
    }

    /**
     * 保存索引数据到文件
     * @param dataIndexs
     */
    @Override
    public void saveDataIndexFile(List<DataIndex> dataIndexs){
        if(dataIndexs != null && !dataIndexs.isEmpty()){
            for (DataIndex dataIndex : dataIndexs){
                String fileName = DateUtil.formatDate(new Date(dataIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
                String dataIndexFilePath = getDataIndexFilePath(fileName);
                String dataIndexBackupFilePath = getDataIndexBackupFilePath(fileName);
                File dataIndexFile = new File(dataIndexFilePath);
                File dataIndexBackupFile = new File(dataIndexBackupFilePath);
                if(dataIndexBackupFile.exists()){
                    dataIndexBackupFile.delete();
                }
                if(dataIndexFile.exists()){
                    dataIndexFile.renameTo(dataIndexBackupFile);
                    dataIndexFile.delete();
                }
                FileUtil.appendLine(dataIndexFile, DataIndexHelper.getDataLine(dataIndex));
            }
        }
    }

    /**
     * 读数据索引文件到内存
     * @return
     */
    @Override
    public List<DataIndex> listDataIndex(){
        return listDataIndex(null);
    }

    /**
     * 读数据索引文件到内存
     * @param startDate
     * @return
     */
    @Override
    public List<DataIndex> listDataIndex(Date startDate){
        List<DataIndex> dataIndices = new ArrayList<>();
        File[] childFiles = FileUtil.listChildFile(dataIndexFilePath, DATA_INDEX_FILE_SUFFIX);
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles){
                String fileName = childFile.getName();
                Date date = DateUtil.parseDate(fileName, DateUtil.YYYY_MM_DD);
                if(startDate != null && date.before(startDate)){
                    continue;
                }
                List<String> dataLines = FileUtil.readLines(childFile);
                if(dataLines != null && !dataLines.isEmpty()){
                    List<DataIndex> childDataIndex = dataLines.stream().map(DataIndexHelper::getDataIndex).collect(Collectors.toList());
                    dataIndices.addAll(childDataIndex);
                }
            }
        }
        return dataIndices;

    }

    @Override
    public void removeDataIndex(Date date) {
        String filePath = getDataIndexFilePath(DateUtil.formatDate(date, DateUtil.YYYY_MM_DD));
        File dataItemFile = new File(filePath);
        if(dataItemFile.exists()){
            dataItemFile.delete();
        }
    }
}
