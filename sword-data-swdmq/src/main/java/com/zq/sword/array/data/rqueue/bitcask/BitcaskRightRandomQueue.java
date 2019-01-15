package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;
import com.zq.sword.array.data.rqueue.RightRandomQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: Bitcask存储模型对列
 * @author: zhouqi1
 * @create: 2018-10-17 15:20
 **/
public class BitcaskRightRandomQueue implements RightRandomQueue<SwordData> {

    private Logger logger = LoggerFactory.getLogger(BitcaskRightRandomQueue.class);

    /**
     * 数据处理器
     */
    private SwordDataProcessor swordDataProcessor;

    /**
     * 数据索引处理器
     */
    private SwordIndexProcessor swordIndexProcessor;

    private List<DataEventListener<SwordData>> dataEventListeners;

    private DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge;

    public BitcaskRightRandomQueue(BitcaskConfig bitcaskConfig){
        logger.info("BitcaskRightRandomQueue init...");
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
    public void bindingDataCycleDisposeBridge(DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge) {
        this.dataCycleDisposeBridge = dataCycleDisposeBridge;
    }

    @Override
    public void registerSwordDataListener(DataEventListener<SwordData> swordDataListener) {
        dataEventListeners.add(swordDataListener);
    }

    @Override
    public Long getLastDataId() {
        return swordDataProcessor.getLastSwordDataId();
    }

    @Override
    public boolean push(SwordData swordData) {

        //过滤掉循环数据
        if(dataCycleDisposeBridge != null && dataCycleDisposeBridge.isCycleData(swordData.getValue())){
            return false;
        }

        SwordIndex swordIndex = swordDataProcessor.addSwordData(swordData);
        swordIndexProcessor.addSwordIndex(swordIndex);

        //数据添加通知监听器
        if(dataEventListeners != null && !dataEventListeners.isEmpty()){
            for(DataEventListener<SwordData> dataDataEventListener : dataEventListeners){
                DataEvent<SwordData> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.SWORD_DATA_ADD);
                dataEvent.setData(swordData);
                dataDataEventListener.listen(dataEvent);
            }
        }
        return true;
    }

    @Override
    public SwordData poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SwordData> selectAfterId(Long id) {
        return selectAfterId(id, null);
    }

    @Override
    public List<SwordData> selectAfterId(Long id, Integer maxNum) {
        SwordIndex swordIndex = swordIndexProcessor.getSwordIndex(id);
        if(swordIndex == null){
            logger.error("无法获取指定ID{}的索引数据", id);
            return null;
        }
        return swordDataProcessor.listSwordDataAfterIndex(swordIndex, maxNum);
    }
}
