package com.zq.sword.array.data.rqueue.bitcask.index;

import com.zq.sword.array.common.utils.DateUtil;
import com.zq.sword.array.common.utils.FileUtil;
import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.data.SwordSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据索引处理器
 * @author: zhouqi1
 * @create: 2018-10-17 15:55
 **/
public class SwordIndexProcessor {

    private Logger logger = LoggerFactory.getLogger(SwordIndexProcessor.class);

    /**
     * 文件后缀
     */
    private static final String DATA_INDEX_FILE_SUFFIX = ".index";

    /**
     * 数据索引路径
     */
    private String indexFilePath;


    /**
     * 数据ID->数据索引数据 字典
     */
    private Map<Long, SwordIndex> swordIndexDic;

    /**
     * 时间->数据ID集合 字典
     */
    private Map<String, List<Long>> dataIdTimeDic;

    /**
     * 最大添加数据索引值
     */
    private int maxAddSwordIndexQueueSize = 1000;

    /**
     * 添加数据索引的最大间隔时间
     */
    private int maxAddSwordIndexQueueFreeTime = 5000;

    /**
     * 索引历史数据保存的天数
     */
    private int dataItemFileRetainDays = 7;

    /**
     * 索引数据缓存对列
     */
    private ConcurrentLinkedQueue<SwordIndex> swordIndexQueue;

    /**
     * 数据索引后台任务执行器
     */
    private SwordIndexBackgroundExecutor swordIndexBackgroundExecutor;

    /**
     * 数据序列化器
     */
    private SwordSerializer<SwordIndex> swordSerializer;

    /**
     * 数据反序列化器
     */
    private SwordDeserializer<SwordIndex> swordDeserializer;

    public SwordIndexProcessor(String indexFilePath){
        this.indexFilePath = indexFilePath;
        swordIndexDic = new ConcurrentHashMap<>();
        dataIdTimeDic = new ConcurrentHashMap<>();
        swordIndexQueue = new ConcurrentLinkedQueue<>();
        swordSerializer = new SwordIndexSerializer();
        swordDeserializer = new SwordIndexDeserializer();
        swordIndexBackgroundExecutor = new SwordIndexBackgroundExecutor();
    }

    /**
     * 开启
     */
    public void start(){
        loadData();
        startTasks();
    }

    /**
     * 初始化加载数据
     */
    private void loadData() {
        File[] childFiles = FileUtil.listChildFile(indexFilePath, DATA_INDEX_FILE_SUFFIX);
        if(childFiles != null && childFiles.length > 0){
            for (File childFile : childFiles){
                List<String> dataLines = FileUtil.readLines(childFile);
                if(dataLines != null && !dataLines.isEmpty()){
                    dataLines.stream().map(c->swordDeserializer.deserialize(c.getBytes())).forEach(c->addSwordIndex(c));
                }
            }
        }
    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //定时内存索引持久化
        swordIndexBackgroundExecutor.execute(()->{
            long prevTime = System.currentTimeMillis();
            while(true){
                List<SwordIndex> dataIndices = new ArrayList<>();
                while (!swordIndexQueue.isEmpty()){
                    dataIndices.add(swordIndexQueue.poll());
                    if(dataIndices.size() >= maxAddSwordIndexQueueSize || (System.currentTimeMillis() - prevTime >= maxAddSwordIndexQueueFreeTime)){
                        persistenceSwordIndexFile(dataIndices);
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
        swordIndexBackgroundExecutor.timedExecute(()->{
            removeSwordIndex(DateUtil.minusDays(new Date(), dataItemFileRetainDays));
        }, 1, TimeUnit.DAYS);
    }

    /**
     * 添加数据索引
     * @param swordIndex 数据索引
     */
    public void addSwordIndex(SwordIndex swordIndex){
        //内存数据维护
        Long dataId = swordIndex.getDataId();
        swordIndexDic.put(dataId, swordIndex);
        String date = DateUtil.formatDate(new Date(swordIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
        if(dataIdTimeDic.containsKey(date)){
            dataIdTimeDic.get(date).add(dataId);
        }else {
            List<Long> dataIdList = new ArrayList<>();
            dataIdList.add(dataId);
            dataIdTimeDic.put(date, dataIdList);
        }

        //添加到数据ID缓存对列
        swordIndexQueue.add(swordIndex);
    }

    /**
     *
     * 获取数据索引
     * @param dataId
     * @return
     */
    public SwordIndex getSwordIndex(Long dataId){
        return swordIndexDic.get(dataId);
    }

    /**
     * 获取索引文件路径
     * @return
     */
    private String getSwordIndexFilePath(String fileName){
        return String.format("%s/%s.index", indexFilePath, fileName);
    }
    /**
     * 获取索引文件备份路径
     * @return
     */
    private String getSwordIndexBackupFilePath(String fileName){
        return String.format("%s/%s.backup", indexFilePath, fileName);
    }

    /**
     * 持久化数据到索引文件中
     * @param swordIndexs
     */
    private void persistenceSwordIndexFile(List<SwordIndex> swordIndexs){
        if(swordIndexs != null && !swordIndexs.isEmpty()){
            for (SwordIndex swordIndex : swordIndexs){
                String fileName = DateUtil.formatDate(new Date(swordIndex.getTimestamp()), DateUtil.YYYY_MM_DD);
                String swordIndexFilePath = getSwordIndexFilePath(fileName);
                String swordIndexBackupFilePath = getSwordIndexBackupFilePath(fileName);
                File swordIndexFile = new File(swordIndexFilePath);
                File swordIndexBackupFile = new File(swordIndexBackupFilePath);
                if(swordIndexBackupFile.exists()){
                    swordIndexBackupFile.delete();
                }
                if(swordIndexFile.exists()){
                    swordIndexFile.renameTo(swordIndexBackupFile);
                    swordIndexFile.delete();
                }
                FileUtil.appendLine(swordIndexFile, new String(swordSerializer.serialize(swordIndex)));
            }
        }
    }

    /**
     * 删除指定时间的数据文件
     * @param date 时间
     */
    private void removeSwordIndex(Date date) {
        List<Long> dataIdList = dataIdTimeDic.get(DateUtil.formatDate(date, DateUtil.YYYY_MM_DD));
        if(dataIdList != null && !dataIdList.isEmpty()){
            for(Long dataId : dataIdList){
                swordIndexDic.remove(dataId);
            }
            dataIdTimeDic.remove(date);
        }

        String filePath = getSwordIndexFilePath(DateUtil.formatDate(date, DateUtil.YYYY_MM_DD));
        File dataItemFile = new File(filePath);
        if(dataItemFile.exists()){
            dataItemFile.delete();
        }
    }

}
