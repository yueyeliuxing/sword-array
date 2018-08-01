package com.zq.sword.array.data.rqueue.manager.impl;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.data.rqueue.domain.DataIndex;
import com.zq.sword.array.data.rqueue.manager.MemoryDataIndexManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: sword-array
 * @description: 内存数据索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:46
 **/
public class MemoryDataIndexManagerImpl implements MemoryDataIndexManager {

    /**
     * 数据ID->数据索引数据 字典
     */
    private Map<Long, DataIndex> dataIndexDic;

    /**
     * 时间->数据ID集合 字典
     */
    private Map<String, List<Long>> dataIdTimeDic;

    public MemoryDataIndexManagerImpl(){
        dataIndexDic = new ConcurrentHashMap<>();
        dataIdTimeDic = new ConcurrentHashMap<>();
    }

    @Override
    public void load(List<DataIndex> dataIndexs){
        if(dataIndexs != null && !dataIndexs.isEmpty()){
            dataIndexs.forEach(dataIndex -> {
                addDataIndex(dataIndex);
            });
        }
    }

    @Override
    public void addDataIndex(DataIndex dataIndex){
        Long dataId = dataIndex.getDataId();
        dataIndexDic.put(dataId, dataIndex);

        String date = DateUtil.formatDate(new Date(dataIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
        if(dataIdTimeDic.containsKey(date)){
            dataIdTimeDic.get(date).add(dataId);
        }else {
            List<Long> dataIdList = new ArrayList<>();
            dataIdList.add(dataId);
            dataIdTimeDic.put(date, dataIdList);
        }
    }

    /**
     * 获取指定Id的数据索引
     * @param dataId
     * @return
     */
    @Override
    public DataIndex getDataIndex(Long dataId){
        return dataIndexDic.get(dataId);
    }

    /**
     * 删除指定日期的数据索引
     * @param date
     */
    @Override
    public void removeDataIndex(Date date){
        List<Long> dataIdList = dataIdTimeDic.get(DateUtil.formatDate(date, DateUtil.YYYY_MM_DD));
        if(dataIdList != null && !dataIdList.isEmpty()){
            for(Long dataId : dataIdList){
                dataIndexDic.remove(dataId);
            }
            dataIdTimeDic.remove(date);
        }
    }


}
