package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.event.DataEvent;
import com.zq.sword.array.common.event.DataEventListener;
import com.zq.sword.array.common.event.DataEventType;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.bridge.DataCycleDisposeBridge;
import com.zq.sword.array.data.lqueue.LeftOrderlyQueue;
import com.zq.sword.array.data.lqueue.QueueState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: bitcask类型队列
 * @author: zhouqi1
 * @create: 2018-10-17 19:12
 **/
public class BitcaskLeftOrderlyQueue implements LeftOrderlyQueue<SwordData> {

    private Logger logger = LoggerFactory.getLogger(BitcaskLeftOrderlyQueue.class);

    private volatile QueueState state;

    private OrderSwordDataProcessor orderSwordDataProcessor;

    private DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge;

    private List<DataEventListener<SwordData>> dataEventListeners;


    public BitcaskLeftOrderlyQueue(String dataFilePath){
        state = QueueState.NEW;

        dataEventListeners = new ArrayList<>();

        orderSwordDataProcessor = new OrderSwordDataProcessor(dataFilePath, this);
        orderSwordDataProcessor.start();

        state = QueueState.START;
    }

    @Override
    public void bindingDataCycleDisposeBridge(DataCycleDisposeBridge<SwordCommand> dataCycleDisposeBridge) {
        this.dataCycleDisposeBridge = dataCycleDisposeBridge;
    }

    @Override
    public void registerSwordDataListener(DataEventListener<SwordData> dataEventListener) {

    }

    @Override
    public Long getLastDataId() {
        return orderSwordDataProcessor.getLastDataId();
    }

    @Override
    public boolean push(SwordData swordData) {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能添加", state.name());
            return false;
        }
        orderSwordDataProcessor.addSwordData(swordData);

        //数据添加通知监听器
        if(dataEventListeners != null && !dataEventListeners.isEmpty()){
            for(DataEventListener<SwordData> dataDataEventListener : dataEventListeners){
                DataEvent<SwordData> dataEvent = new DataEvent<>();
                dataEvent.setType(DataEventType.NODE_DATA_ITEM_CHANGE);
                dataEvent.setData(swordData);
                dataDataEventListener.listen(dataEvent);
            }
        }
        return true;
    }

    @Override
    public SwordData poll() {
        if(state != QueueState.START){
            logger.warn("队列状态是：{}，不能获取数据", state.name());
            return null;
        }

        SwordData swordData = orderSwordDataProcessor.pollSwordData();
        if(swordData != null && dataCycleDisposeBridge != null){
            dataCycleDisposeBridge.addCycleData(swordData.getValue());
        }
        return swordData;
    }

    @Override
    public List<SwordData> selectAfterId(Long id) {
        return selectAfterId(id, null);
    }

    @Override
    public List<SwordData> selectAfterId(Long id, Integer maxNum) {
        return orderSwordDataProcessor.pollAfterId(id, maxNum);
    }

    public void setState(QueueState state){
        this.state = state;
    }
}
