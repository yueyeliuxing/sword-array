package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.data.SwordData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @program: sword-array
 * @description: 数据索引处理器
 * @author: zhouqi1
 * @create: 2018-10-17 15:55
 **/
public class ConsumedSwordDataProcessor {

    private Logger logger = LoggerFactory.getLogger(ConsumedSwordDataProcessor.class);

    private Set<SwordData> consumedSwordDataSet;

    private ConsumedSwordDataBackgroundExecutor consumedSwordDataBackgroundExecutor;

    public ConsumedSwordDataProcessor(){
        consumedSwordDataSet = new CopyOnWriteArraySet<>();
        consumedSwordDataBackgroundExecutor = new ConsumedSwordDataBackgroundExecutor();
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

    }

    /**
     * 开启任务
     */
    private void startTasks(){

        //只保存两分钟的数据
        consumedSwordDataBackgroundExecutor.timedExecute(()->{
            consumedSwordDataSet.clear();
        }, 2, TimeUnit.MINUTES);
    }

    public void addSwordData(SwordData swordData) {
        consumedSwordDataSet.add(swordData);
    }

    public boolean containsSwordData(SwordData swordData) {
        return consumedSwordDataSet.contains(swordData);
    }
}
