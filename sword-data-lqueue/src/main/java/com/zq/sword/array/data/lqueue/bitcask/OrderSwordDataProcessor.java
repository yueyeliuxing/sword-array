package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.data.SwordSerializer;
import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    private ConcurrentLinkedQueue<OrderSwordData> orderSwordDataQueue;

    private Long lastDataId;

    private static final String DATA_ITEM_FILE_SUFFIX = ".data";

    private static final String DATA_ITEM_FILE_TEMP_SUFFIX = ".temp";

    private static final String DATA_ITEM_DELETE_TAG = "DELETE_TAG";

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
    private SwordSerializer<OrderSwordData> swordSerializer;

    /**
     * 数据反序列化器
     */
    private SwordDeserializer<OrderSwordData> swordDeserializer;

    public OrderSwordDataProcessor(String dataFilePath){
        this.dataFilePath = dataFilePath;
        orderSwordDataQueue = new ConcurrentLinkedQueue<>();
        swordSerializer = new OrderSwordDataSerializer();
        swordDeserializer = new OrderSwordDataDeserializer();
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
        List<OrderSwordData> orderSwordDatas = getOrderSwordData();
        orderSwordDatas.stream().filter(c->!DATA_ITEM_DELETE_TAG.equals(c.getValue())).forEach(c->addOrderSwordData(c));
    }

    private List<OrderSwordData> getOrderSwordData() {
        List<OrderSwordData> orderSwordDatas = new ArrayList<>();
        Map<Long, OrderSwordData> orderSwordDataMap = new HashMap<>();
        File[] childFiles = FileUtil.listChildFile(dataFilePath, DATA_ITEM_FILE_SUFFIX);
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles){
                List<String> dataLines = FileUtil.readLines(childFile);
                if(dataLines != null && !dataLines.isEmpty()){
                   dataLines.stream().map(c->swordDeserializer.deserialize(c.getBytes())).forEach(c->{
                       if(orderSwordDataMap.containsKey(c.getId()) && DATA_ITEM_DELETE_TAG.equals(c.getValue())){
                           orderSwordDataMap.get(c.getId()).setValue(DATA_ITEM_DELETE_TAG);
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
            List<OrderSwordData> orderSwordDatas = getOrderSwordData();
            if(orderSwordDatas != null && !orderSwordDatas.isEmpty()){
                Set<Long> itemIds = new HashSet<>();
                for(OrderSwordData dataItem : orderSwordDatas){
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

                    String dataLine = null;
                    try {
                        dataLine = new String(swordSerializer.serialize(dataItem), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    FileUtil.appendLine(dataItemFile, dataLine);
                    itemIds.add(itemId);
                }
            }
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
     * @param orderSwordData
     */
    public void addOrderSwordData(OrderSwordData orderSwordData){
        orderSwordDataQueue.add(orderSwordData);
        lastDataId = orderSwordData.getId();

        String fileName = DateUtil.formatDate(new Date(orderSwordData.getTimestamp()), DateUtil.YYYY_MM_DD);
        String dataItemFilePath = getDataItemFilePath(fileName);
        File dataItemFile = new File(dataItemFilePath);
        String dataLine = null;
        try {
            dataLine = new String(swordSerializer.serialize(orderSwordData), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        FileUtil.appendLine(dataItemFile, dataLine);
    }
    
    public OrderSwordData pollOrderSwordData() {
        OrderSwordData orderSwordData = orderSwordDataQueue.poll();

        OrderSwordData delOrderSwordData = new OrderSwordData();
        delOrderSwordData.setId(orderSwordData.getId());
        delOrderSwordData.setValue(DATA_ITEM_DELETE_TAG);
        delOrderSwordData.setTimestamp(orderSwordData.getTimestamp());
        delOrderSwordData.setCrc(orderSwordData.getCrc());
        addOrderSwordData(delOrderSwordData);
        return orderSwordData;
    }
    
    public List<OrderSwordData> pollAfterId(Long id, Integer maxNum) {
        List<OrderSwordData> dataItems = new ArrayList<>();
        if(!orderSwordDataQueue.isEmpty()){
            for (OrderSwordData orderSwordData : orderSwordDataQueue){
                if(orderSwordData.getId() >= id && (maxNum == null || dataItems.size() <= maxNum)){
                    dataItems.add(orderSwordData);
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
