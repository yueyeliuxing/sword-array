package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.data.*;
import com.zq.sword.array.data.lqueue.QueueState;
import com.zq.sword.array.data.stream.BitcaskRandomAccessFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据索引处理器
 * @author: zhouqi1
 * @create: 2018-10-17 15:55
 **/
public class OrderSwordDataProcessor {

    private Logger logger = LoggerFactory.getLogger(OrderSwordDataProcessor.class);

    private ConcurrentLinkedQueue<SwordData> orderSwordDataQueue;

    private Long lastDataId;

    private static final String DATA_ITEM_FILE_SUFFIX = ".data";

    private static final String DATA_ITEM_FILE_TEMP_SUFFIX = ".temp";

    private static final SwordCommand DELETE_COMMAND = SwordCommand.DELETE_COMMAND;

    /**
     * 数据文件路径
     */
    private String dataFilePath;

    /**
     * 数据索引后台任务执行器
     */
    private OrderSwordDataBackgroundExecutor orderSwordDataBackgroundExecutor;

    /**
     * 数据序列化器
     */
    private SwordSerializer<SwordData> swordSerializer;

    /**
     * 数据反序列化器
     */
    private SwordDeserializer<SwordData> swordDeserializer;

    private BitcaskLeftOrderlyQueue bitcaskLeftOrderlyQueue;

    public OrderSwordDataProcessor(String dataFilePath, BitcaskLeftOrderlyQueue bitcaskLeftOrderlyQueue){
        this.dataFilePath = dataFilePath;
        this.bitcaskLeftOrderlyQueue = bitcaskLeftOrderlyQueue;
        orderSwordDataQueue = new ConcurrentLinkedQueue<>();
        swordSerializer = new SwordDataSerializer();
        swordDeserializer = new SwordDataDeserializer();
        orderSwordDataBackgroundExecutor = new OrderSwordDataBackgroundExecutor();
    }

    /**
     * 开启
     */
    public void start(){
        initData();
        startTasks();
    }

    /*
     *初始化数据
     */
    private void initData(){
        List<SwordData> orderSwordDatas = getSwordData();
        orderSwordDatas.stream().filter(c->!DELETE_COMMAND.equals(c.getValue())).forEach(c->{
            orderSwordDataQueue.add(c);
            lastDataId = c.getId();
        });
    }

    private List<SwordData> getSwordData() {
        List<SwordData> orderSwordDatas = new ArrayList<>();
        Map<Long, SwordData> orderSwordDataMap = new HashMap<>();
        File[] childFiles = FileUtil.listChildFile(dataFilePath, DATA_ITEM_FILE_SUFFIX);
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles){
                List<SwordData> dataLines = null;
                try{
                    BitcaskRandomAccessFile<SwordData> bitcaskRandomAccessFile = new BitcaskRandomAccessFile(childFile.getAbsolutePath(), "rw", swordSerializer, swordDeserializer);
                    dataLines = bitcaskRandomAccessFile.read(0, null);
                }catch (FileNotFoundException e){
                    logger.error("数据文件不存在", e);
                }catch (IOException e){
                    logger.error("读文件错误", e);
                }

                if(dataLines != null && !dataLines.isEmpty()){
                   dataLines.forEach(c->{
                       if(orderSwordDataMap.containsKey(c.getId()) && DELETE_COMMAND.equals(c.getValue())){
                           orderSwordDataMap.get(c.getId()).setValue(DELETE_COMMAND);
                       }else {
                           orderSwordDataMap.put(c.getId(), c);
                           orderSwordDatas.add(c);
                       }
                   });
                }
            }
        }
        return orderSwordDatas;
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //每隔一天重新合并数据文件
        orderSwordDataBackgroundExecutor.timedExecute(()->{

            bitcaskLeftOrderlyQueue.setState(QueueState.STOP);

            List<SwordData> orderSwordDatas = getSwordData();
            if(orderSwordDatas != null && !orderSwordDatas.isEmpty()){
                Set<Long> itemIds = new HashSet<>();
                Map<String, File> fileMap = new HashMap<>();
                for(SwordData dataItem : orderSwordDatas){
                    Long itemId = dataItem.getId();
                    if(itemIds.contains(itemId)){
                        continue;
                    }
                    String fileName = DateUtil.formatDate(new Date(dataItem.getTimestamp()), DateUtil.YYYY_MM_DD);
                    File dataItemFile = new File(getDataItemFilePath(fileName));
                    if(!fileMap.containsKey(fileName) && dataItemFile.exists()){
                        dataItemFile.delete();
                    }

                    try{
                        BitcaskRandomAccessFile<SwordData> bitcaskRandomAccessFile = new BitcaskRandomAccessFile(dataItemFile.getAbsoluteFile().getAbsolutePath(), "rw", swordSerializer, swordDeserializer);
                        bitcaskRandomAccessFile.write(dataItem);
                    }catch (FileNotFoundException e){
                        logger.error("数据文件不存在", e);
                    }catch (IOException e){
                        logger.error("写文件错误", e);
                    }
                    itemIds.add(itemId);
                    fileMap.put(fileName, dataItemFile);
                }
            }

            bitcaskLeftOrderlyQueue.setState(QueueState.START);
        }, 1, TimeUnit.DAYS);
    }

    /**
     * 获取最后的数据ID
     * @return
     */
    public Long getLastDataId() {
        return lastDataId;
    }

    /**
     * 添加数据
     * @param swordData
     */
    public void addSwordData(SwordData swordData){
        orderSwordDataQueue.add(swordData);
        lastDataId = swordData.getId();

        writeDataFile(swordData);
    }

    private void writeDataFile(SwordData swordData) {
        String fileName = DateUtil.formatDate(new Date(swordData.getTimestamp()), DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(fileName);
        try{
            BitcaskRandomAccessFile<SwordData> bitcaskRandomAccessFile = new BitcaskRandomAccessFile(dataItemFilePath, "rw", swordSerializer, swordDeserializer);
            bitcaskRandomAccessFile.write(swordData);
        }catch (FileNotFoundException e){
            logger.error("数据文件不存在", e);
        }catch (IOException e){
            logger.error("写文件错误", e);
        }
    }

    public SwordData pollSwordData() {
        SwordData swordData = orderSwordDataQueue.poll();
        if(swordData != null){
            SwordData delSwordData = new SwordData();
            delSwordData.setId(swordData.getId());
            delSwordData.setValue(DELETE_COMMAND);
            delSwordData.setTimestamp(swordData.getTimestamp());
            delSwordData.setCrc(swordData.getCrc());
            writeDataFile(delSwordData);
        }
        return swordData;
    }
    
    public List<SwordData> pollAfterId(Long id, Integer maxNum) {
        List<SwordData> dataItems = new ArrayList<>();
        if(!orderSwordDataQueue.isEmpty()){
            for (SwordData swordData : orderSwordDataQueue){
                if(swordData.getId() >= id && (maxNum == null || dataItems.size() <= maxNum)){
                    dataItems.add(swordData);
                }
            }
        }
        return dataItems;
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getDataItemFilePath(String fileName){
        return String.format("%s/%s%s", dataFilePath, fileName, DATA_ITEM_FILE_SUFFIX);
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getDataItemFileTempPath(String fileName){
        return String.format("%s/%s%s", dataFilePath, fileName, DATA_ITEM_FILE_TEMP_SUFFIX);
    }
}
