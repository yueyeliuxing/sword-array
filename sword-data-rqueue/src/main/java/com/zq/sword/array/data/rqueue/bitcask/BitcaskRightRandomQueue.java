package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.rqueue.bitcask.data.SwordData;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import com.zq.sword.array.data.rqueue.bitcask.data.SwordDataProcessor;
import com.zq.sword.array.data.rqueue.bitcask.index.SwordIndex;
import com.zq.sword.array.data.rqueue.bitcask.index.SwordIndexProcessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: Bitcask存储模型对列
 * @author: zhouqi1
 * @create: 2018-10-17 15:20
 **/
public class BitcaskRightRandomQueue implements RightRandomQueue<SwordData> {

    /**
     * 数据处理器
     */
    private SwordDataProcessor swordDataProcessor;

    /**
     * 数据索引处理器
     */
    private SwordIndexProcessor swordIndexProcessor;

    private List<DataEventListener<SwordData>> dataEventListeners;

    public BitcaskRightRandomQueue(BitcaskConfig bitcaskConfig){
        String indexFilePath = bitcaskConfig.getIndexFilePath();
        swordIndexProcessor = new SwordIndexProcessor(indexFilePath);
        swordIndexProcessor.start();

        String dataFilePath = bitcaskConfig.getDataFilePath();
        swordDataProcessor = new SwordDataProcessor(dataFilePath);
        swordDataProcessor.start();

        dataEventListeners = new CopyOnWriteArrayList<>();
    }

    public BitcaskRightRandomQueue(String dataFilePath, String indexFilePath){
        this(BitcaskConfig.BitcaskConfigBuilder.create()
                .setDataFilePath(dataFilePath)
                .setIndexFilePath(indexFilePath)
                .defaultSwordDataDeserializer()
                .defaultSwordDataDeserializer()
                .build());
    }

    @Override
    public void registerSwordDataListener(DataEventListener<SwordData> swordDataListener) {
        dataEventListeners.add(swordDataListener);
    }

    @Override
    public Long getLastSwordDataId() {
        return swordDataProcessor.getLastSwordDataId();
    }

    @Override
    public void push(SwordData swordData) {
        SwordIndex swordIndex = swordDataProcessor.addSwordData(swordData);
        swordIndexProcessor.addSwordIndex(swordIndex);

        //数据添加通知监听器
        if(dataEventListeners != null && !dataEventListeners.isEmpty()){
            for(DataEventListener<SwordData> dataDataEventListener : dataEventListeners){
                DataEvent<SwordData> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_DATA_ITEM_CHANGE);
                dataEvent.setData(swordData);
                dataDataEventListener.listen(dataEvent);
            }
        }
    }

    @Override
    public List<SwordData> pollAfterId(Long id) {
        return pollAfterId(id, null);
    }

    @Override
    public List<SwordData> pollAfterId(Long id, Integer maxNum) {
        SwordIndex swordIndex = swordIndexProcessor.getSwordIndex(id);
        return swordDataProcessor.listSwordDataAfterIndex(swordIndex, maxNum);
    }
}
