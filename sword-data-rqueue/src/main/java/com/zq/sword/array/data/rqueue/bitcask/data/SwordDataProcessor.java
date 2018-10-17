package com.zq.sword.array.data.rqueue.bitcask.data;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.data.rqueue.bitcask.index.SwordIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: sword-array
 * @description: 数据索引处理器
 * @author: zhouqi1
 * @create: 2018-10-17 15:55
 **/
public class SwordDataProcessor {

    private Logger logger = LoggerFactory.getLogger(SwordDataProcessor.class);

    private static final String DATA_ITEM_FILE_SUFFIX = ".data";

    private static final String LAST_DATA_ITEM_ID_FILE_NAME = "last_data_item.id";

    /**
     * 历史数据保存的天数
     */
    private int dataItemFileRetainDays = 7;

    /**
     * 数据文件路径
     */
    private String dataFilePath;

    /**
     * 数据索引后台任务执行器
     */
    private SwordDataBackgroundExecutor swordDataBackgroundExecutor;

    /**
     * 数据序列化器
     */
    private SwordSerializer<SwordData> swordSerializer;

    /**
     * 数据反序列化器
     */
    private SwordDeserializer<SwordData> swordDeserializer;

    public SwordDataProcessor(String dataFilePath){
        this.dataFilePath = dataFilePath;
        swordSerializer = new SwordDataSerializer();
        swordDeserializer = new SwordDataDeserializer();
        swordDataBackgroundExecutor = new SwordDataBackgroundExecutor();
    }

    /**
     * 开启
     */
    public void start(){
        startTasks();
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //定时删除索引文件
        swordDataBackgroundExecutor.timedExecute(()->{
            removeDataItem(DateUtil.minusDays(new Date(), dataItemFileRetainDays));
        }, 1, TimeUnit.DAYS);
    }

    /**
     *
     * 获取最新的数据ID
     * @return
     */
    public Long getLastSwordDataId() {
        File lastSwordDataIdFile = new File(getLastSwordDataIdFilePath());
        List<String> dataLines = FileUtil.readLines(lastSwordDataIdFile);
        if(dataLines != null && !dataLines.isEmpty()){
            return Long.parseLong(dataLines.get(0));
        }
        return 0L;
    }

    /**
     * 添加数据
     * @param swordData
     * @return
     */
    public SwordIndex addSwordData(SwordData swordData){
        SwordIndex swordIndex = new SwordIndex();
        String fileName = DateUtil.formatDate(new Date(swordData.getTimestamp()), DateUtil.YYYY_MM_DD);
        String swordDataFilePath = getSwordDataFilePath(fileName);
        File swordDataFile = new File(swordDataFilePath);

        long pos = swordDataFile.length();
        swordIndex.setValuePosition(pos);

        String dataLine = null;
        try {
            dataLine = new String(swordSerializer.serialize(swordData), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        FileUtil.appendLine(swordDataFile, dataLine);

        swordIndex.setDataId(swordData.getId());
        swordIndex.setFileId(fileName);
        swordIndex.setTimestamp(swordData.getTimestamp());
        swordIndex.setValueLength(Long.valueOf(dataLine.length()));


        //保存iD
        File lastSwordDataIdFile = new File(getLastSwordDataIdFilePath());
        if(lastSwordDataIdFile.exists()){
            lastSwordDataIdFile.delete();
        }
        FileUtil.appendLine(lastSwordDataIdFile, String.valueOf(swordData.getId()));
        return swordIndex;
    }

    /**
     * 获取指定索引之后的数据
     * @param swordIndex
     * @param num
     * @return
     */
    public List<SwordData> listSwordDataAfterIndex(SwordIndex swordIndex, Integer num){
        List<SwordData> dataIndices = new ArrayList<>();
        long valuePos = swordIndex.getValuePosition();
        String startDateValue = DateUtil.formatDate(new Date(swordIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
        String swordDataFilePath = getSwordDataFilePath(startDateValue);
        List<String> dataLines = FileUtil.readLines(swordDataFilePath, valuePos, num);
        if(dataLines != null && !dataLines.isEmpty()){
            dataIndices.addAll(dataLines.stream().map(c->swordDeserializer.deserialize(c.getBytes())).collect(Collectors.toList()));
        }
        return dataIndices;
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getSwordDataFilePath(String fileName){
        return String.format("%s/%s%s", dataFilePath, fileName, DATA_ITEM_FILE_SUFFIX);
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getLastSwordDataIdFilePath(){
        return String.format("%s/%s", dataFilePath, LAST_DATA_ITEM_ID_FILE_NAME);
    }

    /**
     * 删除指定时间的数据文件
     * @param date
     */
    public void removeDataItem(Date date){
        String dateValue = DateUtil.formatDate(date, DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getSwordDataFilePath(dateValue);
        File dataItemFile = new File(dataItemFilePath);
        if(dataItemFile.exists()){
            dataItemFile.delete();
        }
    }
}
